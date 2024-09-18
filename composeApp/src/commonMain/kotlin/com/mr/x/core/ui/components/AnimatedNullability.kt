package com.mr.x.core.ui.components

import androidx.compose.animation.*
import androidx.compose.runtime.*
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
