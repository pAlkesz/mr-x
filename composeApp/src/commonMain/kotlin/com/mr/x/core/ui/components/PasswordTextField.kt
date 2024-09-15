package com.mr.x.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import mrx.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PasswordTextField(
	modifier: Modifier = Modifier,
	value: String,
	isError: Boolean,
	isPasswordShown: Boolean,
	supportingText: StringResource?,
	shape: Shape = CustomRoundedCornerShape(),
	onDone: () -> Unit,
	onValueChange: (String) -> Unit,
	onShowHidePasswordClicked: () -> Unit
) {
	BaseTextField(
		modifier = modifier.fillMaxWidth(),
		value = value,
		isError = isError,
		imeAction = ImeAction.Done,
		keyboardActions = KeyboardActions(onDone = { onDone() }),
		onValueChange = onValueChange,
		label = { Text(text = stringResource(Res.string.password_field_label)) },
		placeholder = { Text(text = stringResource(Res.string.password_field_placeholder)) },
		keyboardCapitalization = KeyboardCapitalization.None,
		shape = shape,
		visualTransformation = if (isPasswordShown) {
			VisualTransformation.None
		}
		else {
			PasswordVisualTransformation()
		},
		keyboardType = KeyboardType.Password,
		supportingText = {
			AnimatedNullability(supportingText) {
				Text(text = stringResource(it))
			}
		},
		trailingIcon = {
			CrossFade(
				condition = isPasswordShown,
				onConditionFalse = {
					IconButton(
						onClick = onShowHidePasswordClicked) {
						Icon(
							imageVector = vectorResource(Res.drawable.visibility_off),
							contentDescription = null
						)
					}
				},
				onConditionTrue = {
					IconButton(onClick = onShowHidePasswordClicked) {
						Icon(
							imageVector = vectorResource(Res.drawable.visibility_on),
							contentDescription = null
						)
					}
				},
				modifier = Modifier.padding(end = 8.dp)
			)
		}
	)
}
