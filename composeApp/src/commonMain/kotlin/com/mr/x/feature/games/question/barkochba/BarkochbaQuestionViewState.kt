package com.mr.x.feature.games.question.barkochba

import org.jetbrains.compose.resources.StringResource

data class BarkochbaQuestionViewState(
	val text: String = "",
	val isTextInvalid: Boolean = false,
	val event: BarkochbaQuestionEvent? = null
)

sealed interface BarkochbaQuestionEvent {
	data class ValidationError(val message: StringResource) : BarkochbaQuestionEvent
	data class NavigateUp(val uuid: String, val message: StringResource) : BarkochbaQuestionEvent
}

