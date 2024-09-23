package com.palkesz.mr.x.feature.games.question.choose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.usecase.game.GetAndObserveGameUseCase
import com.palkesz.mr.x.core.usecase.game.GetBarkochbaQuestionCountUseCase
import com.palkesz.mr.x.core.util.networking.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.game_ended_message

interface ChooseQuestionViewModel {

    val viewState: StateFlow<ChooseQuestionViewState>

    fun onNormalQuestionClicked()
    fun onBarkochbaQuestionClicked()
    fun onEventHandled()
    fun setGameId(gameId: String)
}

class ChooseQuestionViewModelImpl(
    private val getBarkochbaQuestionCountUseCase: GetBarkochbaQuestionCountUseCase,
    private val getAndObserveGameUseCase: GetAndObserveGameUseCase
) : ViewModel(), ChooseQuestionViewModel {

    private var gameId: String? = null

    private val _viewState = MutableStateFlow(ChooseQuestionViewState())
    override val viewState = _viewState.asStateFlow()

    override fun onNormalQuestionClicked() {
        gameId?.let { gameId ->
            _viewState.update { currentState ->
                currentState.copy(event = ChooseQuestionEvent.NormalQuestionClicked(gameId))
            }
        }
    }

    override fun onBarkochbaQuestionClicked() {
        gameId?.let { gameId ->
            _viewState.update { currentState ->
                currentState.copy(event = ChooseQuestionEvent.BarkochbaQuestionClicked(gameId))
            }
        }
    }

    override fun onEventHandled() {
        _viewState.update { currentState ->
            currentState.copy(event = null)
        }
    }

    override fun setGameId(gameId: String) {
        this.gameId = gameId
        countBarkochba()
        observeGameStatus()
    }

    private fun countBarkochba() {
        gameId?.let { gameId ->
            viewModelScope.launch {
                getBarkochbaQuestionCountUseCase.run(gameId).collect { result ->
                    result.onSuccess {
                        _viewState.update { currentState ->
                            currentState.copy(barkochbaCount = it)
                        }
                    }
                }
            }
        }
    }

    private fun observeGameStatus() {
        viewModelScope.launch {
            gameId?.let { gameId ->
                getAndObserveGameUseCase.run(gameId).collect { result ->
                    result.onSuccess { game ->
                        if (game.status == GameStatus.FINISHED) {
                            _viewState.update { currentState ->
                                currentState.copy(
                                    event = ChooseQuestionEvent.NavigateUp(
                                        gameId,
                                        Res.string.game_ended_message
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
