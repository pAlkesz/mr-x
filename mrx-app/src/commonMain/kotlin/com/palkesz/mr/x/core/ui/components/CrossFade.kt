package com.palkesz.mr.x.core.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CrossFade(
	condition: Boolean,
	onConditionTrue: @Composable () -> Unit,
	onConditionFalse: @Composable () -> Unit,
	modifier: Modifier = Modifier) {
	Crossfade(targetState = condition, modifier = modifier, label = "CrossFade") {
		if (it) {
			onConditionTrue()
		}
		else {
			onConditionFalse()
		}
	}
}
