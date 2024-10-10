package com.palkesz.mr.x.core.ui.providers

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope

val LocalNavController = compositionLocalOf<NavHostController?> {
    null
}

val LocalSnackBarHostState = compositionLocalOf {
    SnackbarHostState()
}

val LocalAppScope = compositionLocalOf<CoroutineScope?> {
    null
}
