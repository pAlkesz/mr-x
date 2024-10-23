package com.palkesz.mr.x.feature.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.usecase.game.FetchGamesUseCase
import com.palkesz.mr.x.core.util.networking.DataLoader
import com.palkesz.mr.x.core.util.networking.RefreshTrigger
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface GamesViewModel {
    val viewState: StateFlow<ViewState<GamesViewState>>

    fun onGameClicked(id: String)
    fun onEventHandled()
    fun onRetry()
}

class GamesViewModelImpl(
    private val fetchGamesUseCase: FetchGamesUseCase,
    private val gameRepository: GameRepository,
    private val gamesUiMapper: GamesUiMapper,
) : ViewModel(), GamesViewModel {

    private val dataLoader = DataLoader<List<Game>>()

    private val refreshTrigger = RefreshTrigger()

    private val loadingResult = dataLoader.loadAndObserveDataAsState(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = ViewState.Loading,
        fetchData = { fetchGamesUseCase.run() },
        observeData = { gameRepository.games },
    )

    private val event = MutableStateFlow<GamesEvent?>(null)

    override val viewState = combine(loadingResult, event) { result, event ->
        result.map { games ->
            GamesViewState(games = gamesUiMapper.mapGames(games = games), event = event)
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
