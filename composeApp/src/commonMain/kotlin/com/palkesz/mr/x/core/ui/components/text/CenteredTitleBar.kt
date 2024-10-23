package com.palkesz.mr.x.core.ui.components.text

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.providers.LocalNavController

@Composable
fun CenteredTitleBar(
    title: String,
    navigationIcon: ImageVector? = Icons.AutoMirrored.Filled.ArrowBack,
    navigationAction: (NavHostController?) -> Unit = { it?.popBackStack() },
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        },
        navigationIcon = {
            NavigationIcon(iconVector = navigationIcon, navigationAction = navigationAction)
        },
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}

@Composable
private fun NavigationIcon(
    iconVector: ImageVector? = Icons.AutoMirrored.Filled.ArrowBack,
    navigationAction: (NavHostController?) -> Unit = {},
) {
    val navController = LocalNavController.current
    AnimatedNullability(item = iconVector) {
        IconButton(
            onClick = { navigationAction(navController) },
            content = { Icon(imageVector = it, contentDescription = null) },
        )
    }
}
