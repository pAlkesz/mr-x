package com.palkesz.mr.x.feature.home.join

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.usecase.game.JoinGameUseCase
import com.palkesz.mr.x.core.util.BUSINESS_TAG
import com.palkesz.mr.x.core.util.networking.DataLoader
import com.palkesz.mr.x.core.util.networking.RefreshTrigger
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.map
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface JoinGameViewModel {
    val viewState: StateFlow<ViewState<JoinGameViewState>>

    fun onQrCodeScanned(url: String)
    fun onRetry()
    fun onEventHandled()
}

@Stable
class JoinGameViewModelImpl(
    private val joinGameUseCase: JoinGameUseCase,
    private val gameRepository: GameRepository,
) : ViewModel(), JoinGameViewModel {

    private val dataLoader = DataLoader<Unit>()

    private val refreshTrigger = RefreshTrigger()

    private val event = MutableStateFlow<JoinGameEvent?>(null)

    private var gameId: String? = null

    private val joinGameResult = dataLoader.loadAndObserveDataAsState(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = ViewState.Success(Unit),
        fetchData = { joinGame() },
    )

    override val viewState = combine(joinGameResult, event) { result, event ->
        result.map { JoinGameViewState(event = event) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ViewState.Success(JoinGameViewState()),
    )

    override fun onQrCodeScanned(url: String) {
        gameId = url.split('=').lastOrNull()
        Napier.d(tag = BUSINESS_TAG) { "Qr code scanned: $gameId" }
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }

    override fun onRetry() {
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }

    override fun onEventHandled() {
        event.update { null }
    }

    private suspend fun joinGame() = gameId?.let { id ->
        joinGameUseCase.run(gameId = id).onSuccess {
            val isAlreadyJoined = gameRepository.games.value.any { it.id == id }
            event.update { JoinGameEvent.NavigateToGames(gameId = id.takeIf { !isAlreadyJoined }) }
        }
    } ?: Result.success(Unit)
}
