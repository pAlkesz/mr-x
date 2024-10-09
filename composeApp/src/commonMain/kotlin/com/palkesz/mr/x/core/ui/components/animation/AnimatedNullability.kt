package com.palkesz.mr.x.core.ui.components.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun <T> AnimatedNullability(
	item: T?,
	modifier: Modifier = Modifier,
	enter: EnterTransition = fadeIn() + expandIn(),
	exit: ExitTransition = shrinkOut() + fadeOut(),
	content: @Composable (T) -> Unit,
) {
	val lastNonNullItem = getLastNonNullValue(value = item)
	AnimatedVisibility(
		visible = item != null,
		modifier = modifier,
		enter = enter,
		exit = exit,
	) {
		lastNonNullItem ?: return@AnimatedVisibility
		content(lastNonNullItem)
	}
}

@Composable
fun <T> getLastNonNullValue(value: T?): T? {
	var lastValue by remember { mutableStateOf(value) }
	LaunchedEffect(value) {
		value?.let {
			lastValue = it
		}
	}
	return lastValue
}
