package com.palkesz.mr.x.feature.games.question.specify

import androidx.compose.runtime.Immutable

@Immutable
data class SpecifyQuestionViewState(
    val text: String,
    val oldText: String,
    val expectedAnswer: String,
    val hostAnswer: String,
    val hostName: String,
    val number: Int,
    val owner: String,
    val isTextValid: Boolean = false,
    val isSaveButtonEnabled: Boolean,
    val event: SpecifyQuestionEvent? = null,
)

@Immutable
sealed interface SpecifyQuestionEvent {

    data object NavigateUp : SpecifyQuestionEvent

}
