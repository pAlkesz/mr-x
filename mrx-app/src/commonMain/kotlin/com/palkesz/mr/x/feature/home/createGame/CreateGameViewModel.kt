package com.palkesz.mr.x.feature.home.createGame

import androidx.lifecycle.ViewModel
import com.palkesz.mr.x.core.data.game.GameAnimationEvent
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.util.CoroutineHelper
import com.palkesz.mr.x.core.util.validateName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrx.mrx_app.generated.resources.Res
import mrx.mrx_app.generated.resources.input_error
import mrx.mrx_app.generated.resources.missing_last_name

interface CreateGameViewModel {
	val viewState: StateFlow<CreateGameViewState>

	fun onFirstNameChanged(firstName: String)
	fun onLastNameChanged(lastName: String)
	fun onCreateGameClicked()
	fun onEventHandled()
}

class CreateGameViewModelImpl(
	private val gameRepository: GameRepository
) : ViewModel(), CreateGameViewModel {

	private val _viewState = MutableStateFlow(CreateGameViewState())
	override val viewState = _viewState.asStateFlow()

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

	override fun onCreateGameClicked() {
		if (_viewState.value.lastName.isEmpty()) {
			_viewState.update { currentState ->
				currentState.copy(
					event = CreateGameEvent.ValidationError(Res.string.missing_last_name),
					isLastNameInvalid = true
				)
			}
			return
		}
		if (!validateName(_viewState.value.lastName)) {
			_viewState.update { currentState ->
				currentState.copy(
					event = CreateGameEvent.ValidationError(Res.string.input_error),
					isLastNameInvalid = true
				)
			}
			return
		}
		if (_viewState.value.firstName.isNotEmpty() && !validateName(_viewState.value.firstName)) {
			_viewState.update { currentState ->
				currentState.copy(
					event = CreateGameEvent.ValidationError(Res.string.input_error),
					isFirstNameInvalid = true
				)
			}
			return
		}

		CoroutineHelper.ioScope.launch {
			gameRepository.emitGameEvent(
				event = GameAnimationEvent(
					firstName = _viewState.value.firstName,
					lastName = _viewState.value.lastName)
			)
		}

		_viewState.update { currentState ->
			currentState.copy(
				event = CreateGameEvent.GameCreationInProgress(
					firstName = _viewState.value.firstName,
					lastName = _viewState.value.lastName
				)
			)
		}

	}

	override fun onEventHandled() {
		_viewState.update { currentState ->
			currentState.copy(event = null)
		}
	}

}
