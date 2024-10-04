package com.palkesz.mr.x.feature.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.usecase.game.CreateGameUseCase
import com.palkesz.mr.x.core.usecase.game.GetMyGamesUseCase
import com.palkesz.mr.x.core.util.networking.Error
import com.palkesz.mr.x.core.util.networking.Loading
import com.palkesz.mr.x.core.util.networking.Success
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.updateIfSuccess
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface MyGamesViewModel {
    val viewState: StateFlow<ViewState<MyGamesViewState>>

    fun onRetry()
    fun onGameClicked(gameId: String)
    fun onFilterSelected(filter: MyGamesFilter)
    fun onEventHandled()
    fun onShowQrCodeClicked(gameId: String)

}

class MyGamesViewModelImpl(
    private val getMyGamesUseCase: GetMyGamesUseCase,
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository,
    private val createGameUseCase: CreateGameUseCase
) : ViewModel(), MyGamesViewModel {

    private val _viewState = MutableStateFlow<ViewState<MyGamesViewState>>(ViewState.Loading)
    override val viewState = _viewState.asStateFlow()

    init {
        getMyGames()
    }

    private fun getMyGames() {
        viewModelScope.launch {
            getMyGamesUseCase.run().collect { result ->
                when (result) {
                    is Error -> _viewState.update { ViewState.Failure() }
                    is Loading -> _viewState.update { ViewState.Loading }
                    is Success -> _viewState.update {
                        ViewState.Success(
                            MyGamesViewState(userGames = result.result.sortedBy {
                                it.lastName
                            }.map { game ->
                                GameItem(
                                    initial = game.lastName.first().uppercaseChar(),
                                    name = if (game.hostId == authRepository.userId ||
                                        game.status == GameStatus.FINISHED
                                    )
                                        "${game.firstName} ${game.lastName}".trim()
                                    else
                                        null,
                                    uuid = game.uuid,
                                    status = game.status,
                                    isHost = game.hostId == authRepository.userId
                                )
                            }.toPersistentList())
                        )
                    }
                }
                observeNewGames()
            }
        }
    }

    private fun observeNewGames() {
        viewModelScope.launch {
            gameRepository.gameAnimationEvent.collect {
                it?.let {
                    createGameUseCase.createGame(it.firstName, it.lastName)
                    gameRepository.emitGameEvent(null)
                }
            }
        }
    }

    override fun onGameClicked(gameId: String) {
        _viewState.updateIfSuccess { state ->
            state.copy(event = GameClickedEvent.GameClicked(gameId))
        }
    }

    override fun onFilterSelected(filter: MyGamesFilter) {
        _viewState.updateIfSuccess { currentState ->
            val selectedFilters =
                if (currentState.selectedFilters.contains(filter)) {
                    (currentState.selectedFilters - filter).toPersistentList()
                } else {
                    (currentState.selectedFilters + filter).toPersistentList()
                }
            currentState.copy(
                selectedFilters = selectedFilters,
                userGames = gameRepository.playerGames.value.sortedBy {
                    it.lastName
                }.mapNotNull { game ->
                    GameItem(
                        initial = game.lastName.first().uppercaseChar(),
                        name = if (game.hostId == authRepository.userId ||
                            game.status == GameStatus.FINISHED
                        )
                            "${game.firstName} ${game.lastName}".trim()
                        else
                            null,
                        uuid = game.uuid,
                        status = game.status,
                        isHost = game.hostId == authRepository.userId
                    ).takeIf { it.isFilteredOut(selectedFilters) }
                }.toPersistentList()
            )
        }
    }

    override fun onEventHandled() {
        _viewState.updateIfSuccess { state ->
            state.copy(event = null)
        }
    }

    override fun onShowQrCodeClicked(gameId: String) {
        _viewState.updateIfSuccess { state ->
            state.copy(event = GameClickedEvent.ShowQrCodeClicked(gameId))
        }
    }

    override fun onRetry() {
        getMyGames()
    }

}
