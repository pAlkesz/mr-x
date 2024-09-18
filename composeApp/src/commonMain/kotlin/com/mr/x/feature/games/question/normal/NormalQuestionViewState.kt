package com.mr.x.feature.games.question.normal

import org.jetbrains.compose.resources.StringResource

data class NormalQuestionViewState(
	val text: String = "",
	val firstName: String = "",
	val lastName: String = "",
	val isTextInvalid: Boolean = false,
	val isLastNameInvalid: Boolean = false,
	val isFirstNameInvalid: Boolean = false,
	val event: NormalQuestionEvent? = null
)

sealed interface NormalQuestionEvent {
	data class ValidationError(val message: StringResource) : NormalQuestionEvent
	data class NavigateUp(val gameId: String, val message: StringResource) : NormalQuestionEvent
}

