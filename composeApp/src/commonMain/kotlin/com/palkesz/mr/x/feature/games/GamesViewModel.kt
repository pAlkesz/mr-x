package com.palkesz.mr.x.feature.games

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.datastore.MrxDataStore
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.usecase.game.FetchGamesResultUseCase
import com.palkesz.mr.x.core.usecase.game.ObserveGamesResultUseCase
import com.palkesz.mr.x.core.util.networking.DataLoader
import com.palkesz.mr.x.core.util.networking.RefreshTrigger
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface GamesViewModel {
    val viewState: StateFlow<ViewState<GamesViewState>>

    fun onGameClicked(id: String)
    fun onEventHandled()
    fun onRetry()
}

@Stable
class GamesViewModelImpl(
    private val joinedGameId: String?,
    private val fetchGamesResultUseCase: FetchGamesResultUseCase,
    private val observeGamesResultUseCase: ObserveGamesResultUseCase,
    private val gamesUiMapper: GamesUiMapper,
    private val mrxDataStore: MrxDataStore,
    gameRepository: GameRepository,
) : ViewModel(), GamesViewModel {

    private val dataLoader = DataLoader<GamesResult>()

    private val refreshTrigger = RefreshTrigger()

    private val loadingResult = dataLoader.loadAndObserveDataAsState(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = ViewState.Loading,
        fetchData = { fetchGamesResultUseCase.run() },
        observeData = { observeGamesResultUseCase.run() },
    )

    private val event = MutableStateFlow<GamesEvent?>(null)

    override val viewState = combine(
        loadingResult,
        gameRepository.games.flatMapLatest { games ->
            mrxDataStore.observeNotificationCount(gameIds = games.map { it.id })
        },
        event,
    ) { result, notifications, event ->
        result.map { gamesResult ->
            GamesViewState(
                joinedGameId = joinedGameId,
                games = gamesUiMapper.mapGames(result = gamesResult, notifications = notifications),
                event = event,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ViewState.Loading)

    override fun onGameClicked(id: String) {
        event.update { GamesEvent.NavigateToGame(id = id) }
    }

    override fun onEventHandled() {
        event.update { null }
    }

    override fun onRetry() {
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }
}
