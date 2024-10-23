package com.palkesz.mr.x.core.ui.effects

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.palkesz.mr.x.core.ui.providers.LocalAppScope
import com.palkesz.mr.x.core.ui.providers.LocalNavController
import com.palkesz.mr.x.core.ui.providers.LocalSnackBarHostState
import kotlinx.coroutines.CoroutineScope

@Composable
fun <T> HandleEventEffect(
    key1: T?,
    block: suspend CoroutineScope.(T, CoroutineScope?, SnackbarHostState, NavHostController?) -> Unit,
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val appScope = LocalAppScope.current
    val navController = LocalNavController.current
    LaunchedEffect(key1 = key1) {
        key1?.let { block(it, appScope, snackbarHostState, navController) }
    }
}
