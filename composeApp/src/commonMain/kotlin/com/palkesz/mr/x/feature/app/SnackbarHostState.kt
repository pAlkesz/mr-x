package com.palkesz.mr.x.feature.app

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val LocalSnackBarHostState = compositionLocalOf {
    SnackbarHostState()
}

val LocalAppScope = compositionLocalOf<CoroutineScope?> {
    null
}

@Composable
fun ShowSnackbar(message: String) {
    val snackbarHostState = LocalSnackBarHostState.current
    LocalAppScope.current?.launch {
        snackbarHostState.apply {
            currentSnackbarData?.dismiss()
            showSnackbar(message = message)
        }
    }
}
