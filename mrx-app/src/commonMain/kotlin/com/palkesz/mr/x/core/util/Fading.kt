package com.palkesz.mr.x.core.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer


fun Modifier.fadingEdge(brush: Brush) = then(
	graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
		.drawWithContent {
			drawContent()
			drawRect(brush = brush, blendMode = BlendMode.SrcOver)
		}
)