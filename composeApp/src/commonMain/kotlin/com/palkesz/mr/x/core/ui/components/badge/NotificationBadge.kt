package com.palkesz.mr.x.core.ui.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.animation.AnimatedNullability
import com.palkesz.mr.x.core.ui.modifiers.squareSize

@Composable
fun ContentWithBadge(
    modifier: Modifier = Modifier,
    badgeCount: Int?,
    isBadgeBig: Boolean = false,
    badgeOffset: DpOffset = DpOffset.Zero,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        AnimatedNullability(
            item = badgeCount,
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .offset(x = badgeOffset.x, y = badgeOffset.y),
        ) { count ->
            NotificationBadge(count = count, isBig = isBadgeBig)
        }
    }
}

@Composable
fun NotificationBadge(modifier: Modifier = Modifier, isBig: Boolean = false, count: Int) {
    val size = if (isBig) 24.dp else 20.dp
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = size, minHeight = size)
            .background(color = MaterialTheme.colorScheme.error, shape = CircleShape)
            .squareSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onError,
        )
    }
}
