package com.palkesz.mr.x.feature.games.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun GameIcon(modifier: Modifier = Modifier, isBig: Boolean = true, icon: ImageVector) {
    Box(
        modifier = modifier
            .size(size = if (isBig) 40.dp else 32.dp)
            .background(color = MaterialTheme.colorScheme.secondary, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(size = if (isBig) 32.dp else 24.dp),
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSecondary,
            contentDescription = null,
        )
    }
}
