package com.palkesz.mr.x.feature.app

import androidx.compose.runtime.Immutable

@Immutable
data class AppViewState(val isLoggedIn: Boolean, val event: AppEvent? = null)

@Immutable
sealed interface AppEvent {

    data class ShowSnackbar(val message: String) : AppEvent

    data class NavigateToMyGames(val gameId: String) : AppEvent

    data object NavigateToHome : AppEvent

    data object NavigateToAddUsername : AppEvent

}
