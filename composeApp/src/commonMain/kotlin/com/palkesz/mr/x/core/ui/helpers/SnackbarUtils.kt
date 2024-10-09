package com.palkesz.mr.x.core.ui.helpers

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.showSnackbar(snackbarHostState: SnackbarHostState, message: String) {
    launch {
        snackbarHostState.apply {
            currentSnackbarData?.dismiss()
            showSnackbar(message = message)
        }
    }
}
