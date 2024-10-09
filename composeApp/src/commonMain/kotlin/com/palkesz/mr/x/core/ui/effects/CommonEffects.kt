package com.palkesz.mr.x.core.ui.effects

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.palkesz.mr.x.core.ui.providers.AppState
import com.palkesz.mr.x.core.ui.providers.LocalAppScope
import com.palkesz.mr.x.core.ui.providers.LocalAppState
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import com.palkesz.mr.x.core.ui.providers.LocalSnackBarHostState
import kotlinx.coroutines.CoroutineScope

@Composable
fun AppStateEffect(key1: Any? = Unit, block: (AppState) -> Unit) {
    val appState = LocalAppState.current
    DisposableEffect(key1 = key1) {
        block(appState)
        onDispose {
            appState.setScreenTitle()
        }
    }
}

@Composable
fun HandleEventEffect(
    key1: Any?,
    block: suspend CoroutineScope.(CoroutineScope?, SnackbarHostState, NavHostController?) -> Unit,
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val appScope = LocalAppScope.current
    val navController = LocalNavController.current
    LaunchedEffect(key1 = key1) {
        block(appScope, snackbarHostState, navController)
    }
}
