package com.mr.x.core.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape

@Composable
fun CustomRoundedCornerShape(): Shape {
	return RoundedCornerShape(
		topStartPercent = 100, topEndPercent = 5,
		bottomEndPercent = 100, bottomStartPercent = 5)
}

@Composable
fun CustomRoundedCornerShapeMirrored(): Shape {
	return RoundedCornerShape(
		topStartPercent = 5, topEndPercent = 100,
		bottomEndPercent = 5, bottomStartPercent = 100)
}
