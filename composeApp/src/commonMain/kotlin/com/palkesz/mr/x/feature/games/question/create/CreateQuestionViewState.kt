package com.palkesz.mr.x.feature.games.question.create

import androidx.compose.runtime.Immutable

@Immutable
data class CreateQuestionViewState(
    val text: String,
    val firstName: String,
    val lastName: String,
    val isTextValid: Boolean,
    val isLastNameValid: Boolean,
    val isFirstNameValid: Boolean,
    val isCreateButtonEnabled: Boolean,
    val event: CreateQuestionEvent? = null,
)

@Immutable
sealed interface CreateQuestionEvent {

    data class NavigateUp(val gameId: String, val questionId: String) : CreateQuestionEvent

}
