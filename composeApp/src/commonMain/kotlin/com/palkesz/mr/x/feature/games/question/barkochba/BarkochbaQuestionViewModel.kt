package com.palkesz.mr.x.feature.games.question.barkochba

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.usecase.game.GetAndObserveGameUseCase
import com.palkesz.mr.x.core.usecase.game.UploadBarkochbaQuestionUseCase
import com.palkesz.mr.x.core.util.networking.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ask_in_progress
import mrx.composeapp.generated.resources.game_ended_message
import mrx.composeapp.generated.resources.missing_question

interface BarkochbaQuestionViewModel {

    val viewState: StateFlow<BarkochbaQuestionViewState>

    fun onTextChanged(text: String)
    fun onAskQuestionClicked()
    fun onEventHandled()
    fun setGameId(gameId: String)
}

class BarkochbaQuestionViewModelImpl(
    private val uploadBarkochbaQuestionUseCase: UploadBarkochbaQuestionUseCase,
    private val getAndObserveGameUseCase: GetAndObserveGameUseCase
) : ViewModel(), BarkochbaQuestionViewModel {

    private var gameId: String? = null

    private val _viewState = MutableStateFlow(BarkochbaQuestionViewState())
    override val viewState = _viewState.asStateFlow()

    override fun onTextChanged(text: String) {
        _viewState.update { currentState ->
            currentState.copy(
                text = text,
                isTextInvalid = false
            )
        }
    }

    override fun onAskQuestionClicked() {
        when {
            _viewState.value.text.isEmpty() ->
                _viewState.update { currentState ->
                    currentState.copy(
                        event = BarkochbaQuestionEvent.ValidationError(Res.string.missing_question),
                        isTextInvalid = true
                    )
                }

            else -> gameId?.let { gameId ->

                uploadBarkochbaQuestionUseCase.run(gameId = gameId, text = _viewState.value.text)

                _viewState.update { currentState ->
                    currentState.copy(
                        event = BarkochbaQuestionEvent.NavigateUp(
                            gameId,
                            Res.string.ask_in_progress
                        )
                    )
                }
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
        observeGameStatus()
    }

    private fun observeGameStatus() {
        viewModelScope.launch {
            gameId?.let { gameId ->
                getAndObserveGameUseCase.run(gameId).collect { result ->
                    result.onSuccess { game ->
                        if (game.status == GameStatus.FINISHED) {
                            _viewState.update { currentState ->
                                currentState.copy(
                                    event = BarkochbaQuestionEvent.NavigateUp(
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
