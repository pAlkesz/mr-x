package com.palkesz.mr.x.feature.home.createGame

import org.jetbrains.compose.resources.StringResource

data class CreateGameViewState(
	val firstName: String = "",
	val lastName: String = "",
	val isLastNameInvalid: Boolean = false,
	val isFirstNameInvalid: Boolean = false,
	val event: CreateGameEvent? = null
)

sealed interface CreateGameEvent {
	data class ValidationError(val message: StringResource) : CreateGameEvent
	data class GameCreationInProgress(val firstName: String, val lastName: String) : CreateGameEvent
}
