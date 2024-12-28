package com.palkesz.mr.x.core.ui.providers

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import com.palkesz.mr.x.feature.app.appbars.titlebarstate.TitleBarState
import kotlinx.coroutines.CoroutineScope

val LocalTitleBarState = compositionLocalOf {
    TitleBarState()
}

val LocalNavController = compositionLocalOf<NavHostController?> {
    null
}

val LocalSnackBarHostState = compositionLocalOf {
    SnackbarHostState()
}

val LocalAppScope = compositionLocalOf<CoroutineScope?> {
    null
}
