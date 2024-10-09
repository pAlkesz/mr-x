package com.palkesz.mr.x.core.ui.providers

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class AppData(val screenTitle: String? = null, val optionalScreenTitle: String? = null)

@Stable
class AppState {
    var currentAppData by mutableStateOf(AppData())
        private set

    fun setScreenTitle(title: String? = null, optionalHiddenTitle: String? = null) {
        currentAppData =
            currentAppData.copy(screenTitle = title, optionalScreenTitle = optionalHiddenTitle)
    }
}
