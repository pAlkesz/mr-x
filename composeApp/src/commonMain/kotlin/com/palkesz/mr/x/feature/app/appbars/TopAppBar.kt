package com.palkesz.mr.x.feature.app.appbars

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
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.modifiers.debouncedClickable
import com.palkesz.mr.x.core.ui.providers.LocalAppState
import com.palkesz.mr.x.core.ui.providers.LocalNavController

@Composable
fun MrXTopAppBar() {
    val appState = LocalAppState.current
    val navController = LocalNavController.current
    AnimatedNullability(
        item = appState.currentAppData.screenTitle,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
    ) { title ->
        TopAppBar(
            title = {
                ChangeableText(
                    defaultText = title,
                    hiddenText = appState.currentAppData.optionalScreenTitle,
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            navigationIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                        .debouncedClickable { navController?.navigateUp() },
                )
            },
        )
    }
}
