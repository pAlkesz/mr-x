package com.mr.x.feature.home.authentication.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mr.x.core.usecase.user.CreateUserUseCase
import com.mr.x.core.util.Error
import com.mr.x.core.util.Loading
import com.mr.x.core.util.Success
import com.mr.x.core.util.validateEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.*

interface SignupViewModel {

	val viewState: StateFlow<SignupViewState>

	fun onEmailChanged(email: String)
	fun onUsernameChanged(username: String)
	fun onPasswordChanged(password: String)
	fun onSignupClicked()
	fun onLoginClicked()
	fun onShowHidePasswordClicked()
	fun onEventHandled()
}

class SignupViewModelImpl(
	private val createUserUseCase: CreateUserUseCase
) : ViewModel(), SignupViewModel {

	private val _viewState = MutableStateFlow(SignupViewState())
	override val viewState = _viewState.asStateFlow()

	override fun onEmailChanged(email: String) {
		_viewState.update { currentState ->
			currentState.copy(
				email = email,
				emailMessage = null
			)
		}
	}

	override fun onUsernameChanged(username: String) {
		_viewState.update { currentState ->
			currentState.copy(
				username = username,
				usernameMessage = null
			)
		}
	}

	override fun onPasswordChanged(password: String) {
		_viewState.update { currentState ->
			currentState.copy(
				password = password,
				passwordMessage = null
			)
		}
	}

	override fun onSignupClicked() {
		when {
			_viewState.value.email.isEmpty() -> {
				_viewState.update { currentState ->
					currentState.copy(
						emailMessage = Res.string.empty_email_message
					)
				}
				return
			}
			_viewState.value.username.isEmpty() -> {
				_viewState.update { currentState ->
					currentState.copy(
						emailMessage = Res.string.empty_username_message
					)
				}
				return
			}
			_viewState.value.password.isEmpty() -> {
				_viewState.update { currentState ->
					currentState.copy(
						passwordMessage = Res.string.empty_password_message
					)
				}
				return
			}
			!validateEmail(_viewState.value.email) -> {
				_viewState.update { currentState ->
					currentState.copy(
						emailMessage = Res.string.invalid_email_message
					)
				}
				return
			}
			_viewState.value.password.length < 6 -> {
				_viewState.update { currentState ->
					currentState.copy(
						passwordMessage = Res.string.password_too_short_message
					)
				}
				return
			}
		}
		viewModelScope.launch {
			createUserUseCase.run(
				userName = _viewState.value.username,
				email = _viewState.value.email,
				password = _viewState.value.password
			).collect { result ->
				when (result) {
					is Error -> _viewState.update {
						it.copy(
							isLoading = false,
							passwordMessage = Res.string.signup_failure_message
						)
					}
					is Loading -> _viewState.update {
						it.copy(isLoading = true)
					}
					is Success -> _viewState.update {
						it.copy(
							isLoading = false,
							event = SignupEvent.SignupSuccess,
							passwordMessage = null
						)
					}
				}
			}
		}

	}

	override fun onLoginClicked() {
		_viewState.update { currentState ->
			currentState.copy(event = SignupEvent.NavigateToLogin)
		}
	}

	override fun onShowHidePasswordClicked() {
		_viewState.update { currentState ->
			currentState.copy(isPasswordShown = !currentState.isPasswordShown)
		}
	}

	override fun onEventHandled() {
		_viewState.update { currentState ->
			currentState.copy(event = null)
		}
	}

}
