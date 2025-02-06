package com.palkesz.mr.x.core.ui.components.titlebar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.palkesz.mr.x.core.ui.helpers.bold
import com.palkesz.mr.x.core.ui.providers.LocalNavController

@Composable
fun CenteredTitleBar(
    modifier: Modifier = Modifier,
    title: String,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(text = title, style = MaterialTheme.typography.titleMedium.bold())
        },
        navigationIcon = navigationIcon,
        actions = actions,
    )
}

@Composable
fun NavigationIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    navigationAction: (NavHostController?) -> Unit = { it?.popBackStack() },
) {
    val navController = LocalNavController.current
    IconButton(
        modifier = modifier,
        onClick = { navigationAction(navController) },
        content = { Icon(imageVector = imageVector, contentDescription = null) },
    )
}
