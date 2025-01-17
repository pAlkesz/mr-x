package com.palkesz.mr.x.feature.games.question.guess

import androidx.compose.runtime.Immutable

@Immutable
data class GuessQuestionViewState(
    val questionText: String,
    val hostAnswer: String?,
    val hostName: String,
    val number: Int,
    val owner: String,
    val firstName: String,
    val lastName: String,
    val isFirstNameValid: Boolean,
    val isLastNameValid: Boolean,
    val isAnswerButtonEnabled: Boolean,
    val event: GuessQuestionEvent? = null,
)

@Immutable
sealed interface GuessQuestionEvent {

    data class NavigateUp(val gameId: String) : GuessQuestionEvent

}
