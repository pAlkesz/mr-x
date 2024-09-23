package com.palkesz.mr.x.core.util

import androidx.compose.ui.Modifier

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier) =
	if (condition) {
		then(modifier(Modifier))
	}
	else {
		this
	}
