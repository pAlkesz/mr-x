package com.mr.x.feature.home.loading

data class LoadingViewState(
	val event: LoadingEvent? = null
)

sealed interface LoadingEvent{
	data object LoginSuccess : LoadingEvent
	data object LoginFailure : LoadingEvent
}
