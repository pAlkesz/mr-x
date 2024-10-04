package com.palkesz.mr.x.feature.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope

data class AppData(
    val isTopAppBarVisible: Boolean = false,
    val isBottomAppBarVisible: Boolean = false,
    val screenTitle: String = "",
    val optionalScreenTitle: String? = null,
)

val LocalAppState = compositionLocalOf {
    AppState()
}

@Stable
class AppState {
    var currentAppData by mutableStateOf(AppData())
        private set

    fun showTopAppBar() {
        currentAppData =
            currentAppData.copy(isTopAppBarVisible = true, isBottomAppBarVisible = false)
    }

    fun hideAppBars() {
        currentAppData =
            currentAppData.copy(isTopAppBarVisible = false, isBottomAppBarVisible = false)
    }

    fun showBottomAppBar() {
        currentAppData =
            currentAppData.copy(isBottomAppBarVisible = true, isTopAppBarVisible = false)
    }

    fun setScreenTitle(
        title: String,
        optionalHiddenTitle: String? = null,
    ) {
        currentAppData =
            currentAppData.copy(
                screenTitle = title,
                optionalScreenTitle = optionalHiddenTitle,
            )
    }
}

@Composable
fun AppStateEffect(key1: Any? = Unit, block: suspend CoroutineScope.(AppState) -> Unit) {
    val appState = LocalAppState.current
    LaunchedEffect(key1 = key1) {
        block(appState)
    }
}
