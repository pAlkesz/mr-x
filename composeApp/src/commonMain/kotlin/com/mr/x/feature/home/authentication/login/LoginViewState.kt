package com.mr.x.feature.home.authentication.login

import org.jetbrains.compose.resources.StringResource

data class LoginViewState(
	val email: String = "",
	val password: String = "",
	val emailMessage: StringResource? = null,
	val passwordMessage: StringResource? = null,
	val isLoading: Boolean = false,
	val isPasswordShown: Boolean = false,
	val event: LoginEvent? = null
)

sealed interface LoginEvent {
	data object LoginSuccess : LoginEvent
	data object NavigateToSignup : LoginEvent
}
