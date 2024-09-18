package com.mr.x.feature.games.question.normal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mr.x.core.model.game.GameStatus
import com.mr.x.core.usecase.game.GetAndObserveGameUseCase
import com.mr.x.core.usecase.game.UploadNormalQuestionUseCase
import com.mr.x.core.util.onSuccess
import com.mr.x.core.util.validateName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.*

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
	private val getAndObserveGameUseCase: GetAndObserveGameUseCase
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
			!validateName(_viewState.value.lastName) ->
				_viewState.update { currentState ->
					currentState.copy(
						event = NormalQuestionEvent.ValidationError(Res.string.input_error),
						isLastNameInvalid = true
					)
				}
			_viewState.value.firstName.isNotEmpty() && !validateName(_viewState.value.firstName) ->
				_viewState.update { currentState ->
					currentState.copy(
						event = NormalQuestionEvent.ValidationError(Res.string.input_error),
						isFirstNameInvalid = true
					)
				}
			else -> gameId?.let { gameId ->
				uploadNormalQuestionUseCase.run(
					text = _viewState.value.text,
					firstName = _viewState.value.firstName,
					lastName = _viewState.value.lastName,
					gameId = gameId
				)

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
		viewModelScope.launch {
			gameId?.let { gameId ->
				getAndObserveGameUseCase.run(gameId).collect { result ->
					result.onSuccess { game ->
						if (game.status == GameStatus.FINISHED) {
							_viewState.update { currentState ->
								currentState.copy(
									event = NormalQuestionEvent.NavigateUp(
										gameId,
										Res.string.game_ended_message)
								)
							}
						}
					}
				}
			}
		}
	}

}
