package com.palkesz.mr.x.feature.games.answer

import androidx.lifecycle.ViewModel
import com.palkesz.mr.x.core.usecase.game.GetGameAndQuestionUseCase
import com.palkesz.mr.x.core.usecase.question.AnswerQuestionUseCase
import com.palkesz.mr.x.core.util.extensions.validateAsName
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.getOrNull
import com.palkesz.mr.x.core.util.networking.updateIfSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.answer_in_progress
import mrx.composeapp.generated.resources.input_error
import mrx.composeapp.generated.resources.missing_last_name

interface AnswerQuestionViewModel {

    val viewState: StateFlow<ViewState<AnswerQuestionViewState>>

    fun onFirstNameChanged(firstName: String)
    fun onLastNameChanged(lastName: String)
    fun onAnswerQuestionClicked()
    fun onEventHandled()
    fun setNavArguments(questionId: String, gameId: String, isHost: Boolean)
    fun onRetry()

}

class AnswerQuestionViewModelImpl(
    private val answerQuestionUseCase: AnswerQuestionUseCase,
    private val getGameAndQuestionUseCase: GetGameAndQuestionUseCase
) : ViewModel(), AnswerQuestionViewModel {

    private var questionId: String? = null
    private var gameId: String? = null
    private var isHost = false

    private val _viewState = MutableStateFlow<ViewState<AnswerQuestionViewState>>(ViewState.Loading)
    override val viewState = _viewState.asStateFlow()

    override fun onFirstNameChanged(firstName: String) {
        _viewState.updateIfSuccess { currentState ->
            currentState.copy(
                firstName = firstName,
                isFirstNameInvalid = false
            )
        }
    }

    override fun onLastNameChanged(lastName: String) {
        _viewState.updateIfSuccess { currentState ->
            currentState.copy(
                lastName = lastName,
                isLastNameInvalid = false
            )
        }
    }

    override fun onAnswerQuestionClicked() {
        _viewState.value.getOrNull()?.let { viewState ->
            when {
                viewState.lastName.isEmpty() -> {
                    _viewState.updateIfSuccess { currentState ->
                        currentState.copy(
                            event = AnswerQuestionEvent.ValidationError(Res.string.missing_last_name),
                            isLastNameInvalid = true
                        )
                    }
                }

                !viewState.lastName.validateAsName() -> {
                    _viewState.updateIfSuccess { currentState ->
                        currentState.copy(
                            event = AnswerQuestionEvent.ValidationError(Res.string.input_error),
                            isLastNameInvalid = true
                        )
                    }
                }

                viewState.firstName.isNotEmpty() && !viewState.firstName.validateAsName() -> {
                    _viewState.updateIfSuccess { currentState ->
                        currentState.copy(
                            event = AnswerQuestionEvent.ValidationError(Res.string.input_error),
                            isFirstNameInvalid = true
                        )
                    }
                }

                else -> {
                    questionId?.let { questionId ->
                        answerQuestionUseCase.run(
                            questionId = questionId,
                            firstName = viewState.firstName,
                            lastName = viewState.lastName,
                            isHost = isHost
                        )

                        _viewState.updateIfSuccess { currentState ->
                            currentState.copy(
                                event = AnswerQuestionEvent.NavigateUp(Res.string.answer_in_progress)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onEventHandled() {
        _viewState.updateIfSuccess { currentState ->
            currentState.copy(event = null)
        }
    }

    override fun setNavArguments(
        questionId: String,
        gameId: String,
        isHost: Boolean
    ) {
        this.questionId = questionId
        this.isHost = isHost
        this.gameId = gameId
        getGameAndQuestion()
    }

    override fun onRetry() {
        getGameAndQuestion()
    }

    private fun getGameAndQuestion() {
        /*safeLet(gameId, questionId) { gameId, questionId ->
            viewModelScope.launch {
                getGameAndQuestionUseCase.run(gameId, questionId)
                    .collect { result ->
                        when (result) {
                            is Error -> _viewState.update { ViewState.Failure() }
                            is Loading -> _viewState.update { ViewState.Loading }
                            is Success -> {
                                _viewState.update {
                                    ViewState.Success(
                                        AnswerQuestionViewState(
                                            questionText = result.result.second.text,
                                            event = AnswerQuestionEvent.NavigateUp(
                                                Res.string.game_ended_message
                                            ).takeIf {
                                                result.result.first.status == GameStatus.FINISHED
                                            }
                                        )
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }*/
    }
}
