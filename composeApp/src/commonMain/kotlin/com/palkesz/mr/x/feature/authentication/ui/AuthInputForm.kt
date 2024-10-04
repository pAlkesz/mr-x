package com.palkesz.mr.x.feature.authentication.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.button.PrimaryButton
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ic_mrx
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AuthInputForm(
    modifier: Modifier = Modifier,
    value: String,
    title: String,
    isValueValid: Boolean,
    errorMessage: String,
    buttonText: String,
    inputLabel: String,
    onValueChanged: (String) -> Unit,
    onButtonClicked: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }
    ColumnWithMrxIcon(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        OutlinedTextField(
            modifier = Modifier.widthIn(max = 488.dp).fillMaxWidth().focusRequester(focusRequester),
            value = value,
            onValueChange = onValueChanged,
            isError = !isValueValid,
            singleLine = true,
            label = { Text(text = inputLabel) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            supportingText = {
                AnimatedVisibility(!isValueValid) {
                    Text(text = errorMessage)
                }
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            onClick = onButtonClicked,
            enabled = isValueValid,
            text = buttonText,
        )
    }
}

@Composable
fun ColumnWithMrxIcon(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.padding(top = 100.dp, bottom = 8.dp)
                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
            imageVector = vectorResource(Res.drawable.ic_mrx),
            contentDescription = null
        )
        content()
    }
}
