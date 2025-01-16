package com.palkesz.mr.x.feature.app

import androidx.compose.runtime.Immutable
import com.palkesz.mr.x.proto.LocalNotificationType

@Immutable
data class AppViewState(
    val isLoggedIn: Boolean,
    val isOfflineBarVisible: Boolean,
    val gameNotificationCount: Int?,
    val event: AppEvent? = null,
)

@Immutable
sealed interface AppEvent {

    data class NavigateToGames(val gameId: String) : AppEvent

    data class NavigateToGame(val gameId: String, val type: LocalNotificationType) : AppEvent

}
