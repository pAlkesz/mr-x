package com.palkesz.mr.x.feature.app

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class AppData(
	val isTopAppBarVisible: Boolean = false,
	val isBottomAppBarVisible: Boolean = false,
	val screenTitle: String = "",
	val optionalScreenTitle: String? = null
)

@Stable
class AppState {

	var currentAppData by mutableStateOf(AppData())
		private set

	fun showTopAppBar() {
		currentAppData = currentAppData.copy(isTopAppBarVisible = true)
	}

	fun hideTopAppBar() {
		currentAppData = currentAppData.copy(isTopAppBarVisible = false)
	}

	fun showBottomAppBar() {
		currentAppData = currentAppData.copy(isBottomAppBarVisible = true)
	}

	fun hideBottomAppBar() {
		currentAppData = currentAppData.copy(isBottomAppBarVisible = false)
	}

	fun setScreenTitle(title: String, optionalHiddenTitle: String? = null) {
		currentAppData = currentAppData.copy(
			screenTitle = title,
			optionalScreenTitle = optionalHiddenTitle
		)
	}
}
