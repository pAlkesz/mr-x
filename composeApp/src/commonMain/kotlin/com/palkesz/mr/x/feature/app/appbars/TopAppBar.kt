package com.palkesz.mr.x.feature.app.appbars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.ChangeableText
import com.palkesz.mr.x.core.ui.modifiers.debouncedClickable
import com.palkesz.mr.x.feature.app.LocalAppState
import com.palkesz.mr.x.feature.app.LocalNavController

@Composable
fun AnimatedTopAppBar() {
    val appState = LocalAppState.current
    AnimatedVisibility(
        visible = appState.currentAppData.isTopAppBarVisible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
    ) {
        MrXTopAppBar()
    }
}

@Composable
private fun MrXTopAppBar() {
    val appState = LocalAppState.current
    val navController = LocalNavController.current
    TopAppBar(
        title = {
            ChangeableText(
                defaultText = appState.currentAppData.screenTitle,
                hiddenText = appState.currentAppData.optionalScreenTitle,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(40.dp).debouncedClickable { navController?.navigateUp() },
            )
        },
    )
}
