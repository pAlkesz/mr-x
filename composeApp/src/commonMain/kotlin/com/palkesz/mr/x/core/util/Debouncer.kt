package com.palkesz.mr.x.core.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.delay

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

@Composable
fun DebouncedButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	shape: Shape = ButtonDefaults.shape,
	colors: ButtonColors = ButtonDefaults.buttonColors(),
	border: BorderStroke? = null,
	contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
	content: @Composable RowScope.() -> Unit
) {
	val containerColor = if (enabled) colors.containerColor else colors.disabledContainerColor
	val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor

	Surface(
		modifier = modifier.semantics { role = Role.Button }
			.debouncedClickable(enabled = enabled, onClick = onClick),
		shape = shape,
		color = containerColor,
		contentColor = contentColor,
		border = border,
	) {
		Row(
			Modifier
				.defaultMinSize(
					minWidth = ButtonDefaults.MinWidth,
					minHeight = ButtonDefaults.MinHeight
				)
				.padding(contentPadding),
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically,
			content = content
		)

	}
}

private const val ON_CLICK_DEBOUNCE_TIME = 400L
