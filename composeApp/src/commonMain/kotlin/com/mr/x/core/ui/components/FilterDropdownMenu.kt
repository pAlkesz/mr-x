package com.mr.x.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.filter_list
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun <E : ContainsText> FilterDropdownMenu(
	modifier: Modifier = Modifier,
	options: ImmutableList<E>,
	selected: ImmutableList<E>,
	onDismiss: (() -> Unit)? = null,
	onSelected: ((E) -> Unit)? = null
) {
	var expanded by remember { mutableStateOf(false) }

	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
			.clip(RoundedCornerShape(8.dp))
			.clickable { expanded = expanded.not() },
	) {
		Icon(vectorResource(Res.drawable.filter_list), null)
		DropdownMenu(
			expanded = expanded,
			onDismissRequest = {
				expanded = false
				onDismiss?.invoke()
			}
		) {
			options.forEach { selectionOption ->
				DropdownMenuItem(
					onClick = {
						onSelected?.invoke(selectionOption)
					},
					text = {
						Text(text = stringResource(selectionOption.text))
					},
					trailingIcon = {
						Checkbox(
							checked = selected.contains(selectionOption),
							onCheckedChange = {
								onSelected?.invoke(selectionOption)
							}
						)
					}
				)
			}
		}
	}
}

interface ContainsText {
	val text: StringResource
}
