package com.palkesz.mr.x.feature.games.question.normal

import androidx.lifecycle.ViewModel
import com.palkesz.mr.x.core.usecase.game.FetchGameResultUseCase
import com.palkesz.mr.x.core.usecase.game.UploadNormalQuestionUseCase
import com.palkesz.mr.x.core.util.extensions.validateAsName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ask_in_progress
import mrx.composeapp.generated.resources.input_error
import mrx.composeapp.generated.resources.missing_last_name
import mrx.composeapp.generated.resources.missing_question

interface NormalQuestionViewModel {

    val viewState: StateFlow<NormalQuestionViewState>

    fun onTextChanged(text: String)
    fun onFirstNameChanged(firstName: String)
    fun onLastNameChanged(lastName: String)
    fun onAskQuestionClicked()
    fun onEventHandled()
    fun setGameId(gameId: String)
}

class NormalQuestionViewModelImpl(
    private val uploadNormalQuestionUseCase: UploadNormalQuestionUseCase,
    private val getAndObserveGameUseCase: FetchGameResultUseCase
) : ViewModel(), NormalQuestionViewModel {

    private var gameId: String? = null

    private val _viewState = MutableStateFlow(NormalQuestionViewState())
    override val viewState = _viewState.asStateFlow()

    override fun onTextChanged(text: String) {
        _viewState.update { currentState ->
            currentState.copy(
                text = text,
                isTextInvalid = false
            )
        }
    }

    override fun onFirstNameChanged(firstName: String) {
        _viewState.update { currentState ->
            currentState.copy(
                firstName = firstName,
                isFirstNameInvalid = false
            )
        }
    }

    override fun onLastNameChanged(lastName: String) {
        _viewState.update { currentState ->
            currentState.copy(
                lastName = lastName,
                isLastNameInvalid = false
            )
        }
    }

    override fun onAskQuestionClicked() {
        when {
            _viewState.value.text.isEmpty() ->
                _viewState.update { currentState ->
                    currentState.copy(
                        event = NormalQuestionEvent.ValidationError(Res.string.missing_question),
                        isTextInvalid = true
                    )
                }

            _viewState.value.lastName.isEmpty() ->
                _viewState.update { currentState ->
                    currentState.copy(
                        event = NormalQuestionEvent.ValidationError(Res.string.missing_last_name),
                        isLastNameInvalid = true
                    )
                }

            !_viewState.value.lastName.validateAsName() ->
                _viewState.update { currentState ->
                    currentState.copy(
                        event = NormalQuestionEvent.ValidationError(Res.string.input_error),
                        isLastNameInvalid = true
                    )
                }

            _viewState.value.firstName.isNotEmpty() && !_viewState.value.firstName.validateAsName() ->
                _viewState.update { currentState ->
                    currentState.copy(
                        event = NormalQuestionEvent.ValidationError(Res.string.input_error),
                        isFirstNameInvalid = true
                    )
                }

            else -> gameId?.let { gameId ->
                /*uploadNormalQuestionUseCase.run(
                    text = _viewState.value.text,
                    firstName = _viewState.value.firstName,
                    lastName = _viewState.value.lastName,
                    gameId = gameId
                )*/

                _viewState.update { currentState ->
                    currentState.copy(
                        event = NormalQuestionEvent.NavigateUp(gameId, Res.string.ask_in_progress)
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
        /*viewModelScope.launch {
            gameId?.let { gameId ->
                getAndObserveGameUseCase.run(gameId).collect { result ->
                    result.onSuccess { game ->
                        if (game.status == GameStatus.FINISHED) {
                            _viewState.update { currentState ->
                                currentState.copy(
                                    event = NormalQuestionEvent.NavigateUp(
                                        gameId,
                                        Res.string.game_ended_message
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }*/
    }

}
