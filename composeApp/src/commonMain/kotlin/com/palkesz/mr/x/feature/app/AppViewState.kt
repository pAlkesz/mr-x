package com.palkesz.mr.x.feature.app

import androidx.compose.runtime.Immutable

@Immutable
data class AppViewState(
    val isLoggedIn: Boolean,
    val isOfflineBarVisible: Boolean,
    val event: AppEvent? = null,
)

@Immutable
sealed interface AppEvent {

    data class NavigateToMyGames(val gameId: String) : AppEvent

}
