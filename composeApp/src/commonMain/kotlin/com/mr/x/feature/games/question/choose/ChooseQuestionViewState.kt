package com.mr.x.feature.games.question.choose

import org.jetbrains.compose.resources.StringResource

data class ChooseQuestionViewState(
	val barkochbaCount: Int = 0,
	val event: ChooseQuestionEvent? = null
)

sealed interface ChooseQuestionEvent {
	data class NormalQuestionClicked(val gameId: String) : ChooseQuestionEvent
	data class BarkochbaQuestionClicked(val gameId: String) : ChooseQuestionEvent
	data class NavigateUp(val gameId: String, val message: StringResource) : ChooseQuestionEvent
}
