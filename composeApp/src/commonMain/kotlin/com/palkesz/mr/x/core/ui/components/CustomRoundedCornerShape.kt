package com.palkesz.mr.x.core.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape

@Composable
fun CustomRoundedCornerShape(): Shape {
	return RoundedCornerShape(
		topStartPercent = 100, topEndPercent = 5,
		bottomEndPercent = 100, bottomStartPercent = 5)
}
