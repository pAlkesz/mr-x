package com.palkesz.mr.x.feature.home.create

import androidx.compose.runtime.Immutable

@Immutable
data class CreateGameViewState(
    val firstName: String,
    val lastName: String,
    val isLastNameValid: Boolean,
    val isFirstNameValid: Boolean,
    val isCreateButtonEnabled: Boolean,
    val event: CreateGameEvent? = null,
)

@Immutable
sealed interface CreateGameEvent {

    data class NavigateToGames(val gameId: String) : CreateGameEvent

}
