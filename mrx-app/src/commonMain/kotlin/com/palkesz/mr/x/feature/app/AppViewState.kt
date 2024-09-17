package com.palkesz.mr.x.feature.app

import dev.theolm.rinku.DeepLink

data class AppViewState(val event: AppEvent? = null, val deepLink: DeepLink? = null)

sealed interface AppEvent {
	data class ErrorOccurred(val message: String) : AppEvent
	data class DeepLinkReceived(val gameId: String) : AppEvent
}