package com.palkesz.mr.x.feature.games.question.specify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.usecase.game.GetGameAndQuestionUseCase
import com.palkesz.mr.x.core.usecase.game.question.AcceptHostAnswerUseCase
import com.palkesz.mr.x.core.util.extensions.safeLet
import com.palkesz.mr.x.core.util.networking.Error
import com.palkesz.mr.x.core.util.networking.Loading
import com.palkesz.mr.x.core.util.networking.Success
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.getOrNull
import com.palkesz.mr.x.core.util.networking.updateIfSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ask_in_progress
import mrx.composeapp.generated.resources.game_ended_message
import mrx.composeapp.generated.resources.missing_question

interface SpecifyQuestionViewModel {

	val viewState: StateFlow<ViewState<SpecifyQuestionViewState>>

	fun onTextChanged(text: String)
	fun onAskQuestionClicked()
	fun onEventHandled()
	fun setVariablesFromNavigation(questionId: String, gameId: String)
	fun onRetry()
}

class SpecifyQuestionViewModelImpl(
	private val acceptHostAnswerUseCase: AcceptHostAnswerUseCase,
	private val getGameAndQuestionUseCase: GetGameAndQuestionUseCase
) : ViewModel(), SpecifyQuestionViewModel {

	private var questionId: String? = null
	private var gameId: String? = null

	private val _viewState =
		MutableStateFlow<ViewState<SpecifyQuestionViewState>>(ViewState.Loading)
	override val viewState = _viewState.asStateFlow()

	override fun onTextChanged(text: String) {
		_viewState.updateIfSuccess { currentState ->
			currentState.copy(
				text = text,
				isTextInvalid = false
			)
		}
	}

	override fun onAskQuestionClicked() {
		when {
			_viewState.value.getOrNull()?.text?.isEmpty() == true ->
				_viewState.updateIfSuccess { currentState ->
					currentState.copy(
						event = SpecifyQuestionEvent.ValidationError(Res.string.missing_question),
						isTextInvalid = true
					)
				}

			else -> safeLet(questionId, _viewState.value.getOrNull()) { questionId, viewState ->

				acceptHostAnswerUseCase.run(
					questionId = questionId,
					text = viewState.text
				)

				_viewState.updateIfSuccess { currentState ->
					currentState.copy(
						event = SpecifyQuestionEvent.NavigateUp(
							viewState.gameId,
							Res.string.ask_in_progress)
					)
				}
			}
		}
	}

	override fun onEventHandled() {
		_viewState.updateIfSuccess { currentState ->
			currentState.copy(event = null)
		}
	}

	override fun setVariablesFromNavigation(questionId: String, gameId: String) {
		this.questionId = questionId
		this.gameId = gameId
		getGameAndQuestion()
	}

	override fun onRetry() {
		getGameAndQuestion()
	}

	private fun getGameAndQuestion() {
		safeLet(gameId, questionId) { gameId, questionId ->
			viewModelScope.launch {
				getGameAndQuestionUseCase.run(gameId, questionId)
					.collect { result ->
						when (result) {
							is Error -> _viewState.update { ViewState.Failure }
							is Loading -> _viewState.update { ViewState.Loading }
							is Success -> {
								_viewState.update {
									ViewState.Success(
										SpecifyQuestionViewState(
											gameId = gameId,
											oldQuestionText = result.result.second.text,
											text = result.result.second.text,
											expectedAnswer =
											"${result.result.second.expectedFirstName} ${
												result.result.second.expectedLastName
											}".trim(),
											event = SpecifyQuestionEvent.NavigateUp(
												gameId = gameId,
												message = Res.string.game_ended_message
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
	}

}
