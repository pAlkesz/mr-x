package com.palkesz.mr.x.feature.home.authentication.signup

import org.jetbrains.compose.resources.StringResource

data class SignupViewState(
	val email: String = "",
	val username: String = "",
	val password: String = "",
	val emailMessage: StringResource? = null,
	val usernameMessage: StringResource? = null,
	val passwordMessage: StringResource? = null,
	val isLoading: Boolean = false,
	val isPasswordShown: Boolean = false,
	val event: SignupEvent? = null
)

sealed interface SignupEvent {
	data object SignupSuccess : SignupEvent
	data object NavigateToLogin : SignupEvent
}
