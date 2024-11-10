package com.palkesz.mr.x.feature.home.join

import androidx.compose.runtime.Immutable

@Immutable
data class JoinGameViewState(val event: JoinGameEvent? = null)

@Immutable
sealed interface JoinGameEvent {

    data object NavigateToGames : JoinGameEvent

    data object NavigateToHome : JoinGameEvent

}
