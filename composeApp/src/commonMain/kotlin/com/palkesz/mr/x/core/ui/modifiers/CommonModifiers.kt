package com.palkesz.mr.x.core.ui.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.delay

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier) =
    if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }

fun Modifier.fadingEdge(brush: Brush) = then(
    graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            drawRect(brush = brush, blendMode = BlendMode.SrcOver)
        }
)

fun Modifier.debouncedClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
) = then(composed {
    var wasClicked by remember { mutableStateOf(false) }
    LaunchedEffect(wasClicked) {
        if (wasClicked) {
            delay(ON_CLICK_DEBOUNCE_TIME)
            wasClicked = false
        }
    }
    Modifier.clickable(enabled && !wasClicked, onClickLabel, role) {
        wasClicked = true
        onClick()
    }
})

private const val ON_CLICK_DEBOUNCE_TIME = 400L
