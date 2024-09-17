package com.palkesz.mr.x.core.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.theme.AppTypography
import com.palkesz.mr.x.core.ui.theme.primaryLight
import com.palkesz.mr.x.core.ui.theme.tertiaryLight
import mrx.mrx_app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BaseTextField(
	modifier: Modifier = Modifier,
	value: String,
	onValueChange: (String) -> Unit,
	label: @Composable () -> Unit,
	placeholder: @Composable () -> Unit,
	supportingText: @Composable (() -> Unit)? = null,
	trailingIcon: @Composable (() -> Unit)? = null,
	isError: Boolean = false,
	imeAction: ImeAction = ImeAction.Next,
	keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.Words,
	keyboardActions: KeyboardActions = KeyboardActions(),
	keyboardType: KeyboardType = KeyboardType.Text,
	shape: Shape,
	visualTransformation: VisualTransformation = VisualTransformation.None,
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		label = label,
		placeholder = placeholder,
		isError = isError,
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			capitalization = keyboardCapitalization,
			keyboardType = keyboardType,
			imeAction = imeAction,
			autoCorrect = false),
		keyboardActions = keyboardActions,
		shape = shape,
		leadingIcon = {
			Icon(
				modifier = Modifier.size(24.dp),
				imageVector = vectorResource(Res.drawable.spy),
				contentDescription = null
			)
		},
		trailingIcon = trailingIcon,
		colors = texfieldColors(),
		modifier = modifier.padding(24.dp),
		visualTransformation = visualTransformation,
		supportingText = supportingText
	)
}

@Composable
fun texfieldColors() = TextFieldDefaults.colors(
	unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
	unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
	unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSecondaryContainer,
	unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
	unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
	focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
	focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
	focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
	errorContainerColor = MaterialTheme.colorScheme.errorContainer
)

@Composable
fun CodeTextField(
	value: String,
	length: Int = 6,
	modifier: Modifier = Modifier,
	keyboardActions: KeyboardActions = KeyboardActions(),
	onValueChange: (String) -> Unit,
) {
	BasicTextField(
		modifier = modifier.fillMaxWidth().padding(12.dp),
		value = value,
		singleLine = true,
		onValueChange = {
			if (it.length <= length) {
				onValueChange(it)
			}
		},
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Text,
			capitalization = KeyboardCapitalization.Characters,
			imeAction = ImeAction.Done,
			autoCorrect = false),
		keyboardActions = keyboardActions,
		decorationBox = { innerTextField ->
			Box {
				CompositionLocalProvider(
					LocalTextSelectionColors.provides(
						TextSelectionColors(
							Color.Transparent,
							Color.Transparent
						)
					)
				) {
					Box(modifier = Modifier.drawWithContent { }) {
						innerTextField()
					}
				}
				Row(
					Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Center,
				) {
					repeat(length) { index ->
						val currentChar = value.getOrNull(index)
						Box(
							modifier = modifier
								.size(56.dp)
								.border(
									width = 4.dp,
									color = if (currentChar != null) primaryLight else tertiaryLight,
									shape = RoundedCornerShape(8.dp)
								).align(alignment = Alignment.CenterVertically),
						) {
							AnimatedNullability(currentChar) {
								Text(
									modifier = Modifier.fillMaxSize(),
									text = it.toString(),
									textAlign = TextAlign.Center,
									style = AppTypography.headlineLarge
								)
							}
						}
					}
				}
			}
		}
	)
}

@Composable
fun FirstAndLastNameTexFields(
	firstName: String,
	lastName: String,
	isFirstNameInvalid: Boolean,
	isLastNameInvalid: Boolean,
	onFirstNameChanged: (String) -> Unit,
	onLastNameChanged: (String) -> Unit,
	onDone: () -> Unit
) {
	BaseTextField(
		modifier = Modifier.fillMaxWidth(),
		value = firstName,
		onValueChange = {
			onFirstNameChanged(it)
		},
		label = {
			Text(text = stringResource(Res.string.first_name))
		},
		placeholder = {
			Text(text = stringResource(Res.string.enter_first_name))
		},
		imeAction = ImeAction.Next,
		isError = isFirstNameInvalid,
		shape = RoundedCornerShape(
			topStartPercent = 100, topEndPercent = 5,
			bottomEndPercent = 100, bottomStartPercent = 5
		)
	)
	BaseTextField(
		modifier = Modifier.fillMaxWidth(),
		value = lastName,
		onValueChange = {
			onLastNameChanged(it)
		},
		label = {
			Text(text = stringResource(Res.string.last_name))
		},
		placeholder = {
			Text(text = stringResource(Res.string.enter_last_name))
		},
		isError = isLastNameInvalid,
		imeAction = ImeAction.Done,
		keyboardActions = KeyboardActions(onDone = { onDone() }),
		shape = RoundedCornerShape(
			topStartPercent = 5, topEndPercent = 100,
			bottomEndPercent = 5, bottomStartPercent = 100
		)
	)
}
