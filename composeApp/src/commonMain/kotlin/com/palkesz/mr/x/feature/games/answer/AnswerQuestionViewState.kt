package com.palkesz.mr.x.feature.games.answer

import org.jetbrains.compose.resources.StringResource

data class AnswerQuestionViewState(
	val questionText: String = "",
	val firstName: String = "",
	val lastName: String = "",
	val isLastNameInvalid: Boolean = false,
	val isFirstNameInvalid: Boolean = false,
	val event: AnswerQuestionEvent? = null
)

sealed interface AnswerQuestionEvent {
	data class ValidationError(val message: StringResource) : AnswerQuestionEvent
	data class NavigateUp(val message: StringResource) : AnswerQuestionEvent
}
