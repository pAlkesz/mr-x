package com.palkesz.mr.x.feature.home.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.user.AuthRepository
import com.palkesz.mr.x.core.util.Error
import com.palkesz.mr.x.core.util.Loading
import com.palkesz.mr.x.core.util.Success
import com.palkesz.mr.x.core.util.validateEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrx.composeapp.generated.resources.*

interface LoginViewModel {

	val viewState: StateFlow<LoginViewState>

	fun onEmailChanged(email: String)
	fun onPasswordChanged(password: String)
	fun onLoginClicked()
	fun onSignupClicked()
	fun onShowHidePasswordClicked()
	fun onEventHandled()
}

class LoginViewModelImpl(
	private val authRepository: AuthRepository
) : ViewModel(), LoginViewModel {

	private val _viewState = MutableStateFlow(LoginViewState())
	override val viewState = _viewState.asStateFlow()

	override fun onEmailChanged(email: String) {
		_viewState.update { currentState ->
			currentState.copy(
				email = email,
				emailMessage = null
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

	override fun onLoginClicked() {
		if (_viewState.value.email.isEmpty()) {
			_viewState.update { currentState ->
				currentState.copy(
					emailMessage = Res.string.empty_email_message
				)
			}
			return
		}
		if (_viewState.value.password.isEmpty()) {
			_viewState.update { currentState ->
				currentState.copy(
					passwordMessage = Res.string.empty_password_message
				)
			}
			return
		}
		if (!validateEmail(_viewState.value.email)) {
			_viewState.update { currentState ->
				currentState.copy(
					emailMessage = Res.string.invalid_email_message
				)
			}
			return
		}
		viewModelScope.launch {
			authRepository.authenticate(_viewState.value.email, _viewState.value.password)
				.collect { result ->
					when (result) {
						is Error -> _viewState.update {
							it.copy(
								isLoading = false,
								passwordMessage = Res.string.login_failure_message
							)
						}
						is Loading -> _viewState.update {
							it.copy(isLoading = true)
						}
						is Success -> _viewState.update {
							it.copy(
								isLoading = false,
								event = LoginEvent.LoginSuccess,
								passwordMessage = null
							)
						}
					}
				}
		}
	}

	override fun onSignupClicked() {
		_viewState.update { currentState ->
			currentState.copy(event = LoginEvent.NavigateToSignup)
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
