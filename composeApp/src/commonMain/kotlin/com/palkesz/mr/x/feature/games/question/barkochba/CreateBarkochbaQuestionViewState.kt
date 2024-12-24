package com.palkesz.mr.x.feature.games.question.barkochba

import androidx.compose.runtime.Immutable

@Immutable
data class CreateBarkochbaQuestionViewState(
    val text: String,
    val isTextValid: Boolean,
    val isCreateButtonEnabled: Boolean,
    val event: CreateBarkochbaQuestionEvent? = null,
)

@Immutable
sealed interface CreateBarkochbaQuestionEvent {

    data class NavigateUp(val gameId: String, val questionId: String) : CreateBarkochbaQuestionEvent

}
