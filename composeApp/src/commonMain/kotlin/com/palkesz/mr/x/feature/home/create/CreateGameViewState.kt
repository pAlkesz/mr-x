package com.palkesz.mr.x.feature.home.create

import androidx.compose.runtime.Immutable
import com.palkesz.mr.x.core.model.game.Game

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

    data class NavigateToGames(val game: Game) : CreateGameEvent

}
