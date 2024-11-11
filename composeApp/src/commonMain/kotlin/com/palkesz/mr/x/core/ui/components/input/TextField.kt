package com.palkesz.mr.x.core.ui.components.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryTextField(
    modifier: Modifier = Modifier,
    value: String,
    isValueValid: Boolean,
    label: String,
    error: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    showKeyboard: Boolean,
    onValueChanged: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        if (showKeyboard) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }
    OutlinedTextField(
        modifier = modifier.widthIn(max = 488.dp).fillMaxWidth().focusRequester(focusRequester),
        value = value,
        onValueChange = onValueChanged,
        isError = !isValueValid,
        singleLine = true,
        label = { Text(text = label) },
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        supportingText = {
            AnimatedVisibility(!isValueValid) {
                Text(text = error)
            }
        }
    )
}
