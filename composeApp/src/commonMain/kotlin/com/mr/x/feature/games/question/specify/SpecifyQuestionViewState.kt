package com.mr.x.feature.games.question.specify

import org.jetbrains.compose.resources.StringResource

data class SpecifyQuestionViewState(
	val text: String = "",
	val oldQuestionText: String,
	val gameId: String,
	val expectedAnswer: String,
	val isTextInvalid: Boolean = false,
	val event: SpecifyQuestionEvent? = null
)

sealed interface SpecifyQuestionEvent {
	data class ValidationError(val message: StringResource) : SpecifyQuestionEvent
	data class NavigateUp(val gameId: String, val message: StringResource) : SpecifyQuestionEvent
}

