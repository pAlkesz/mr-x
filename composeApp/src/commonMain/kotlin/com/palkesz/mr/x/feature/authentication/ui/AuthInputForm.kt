package com.palkesz.mr.x.feature.authentication.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.button.PrimaryButton
import com.palkesz.mr.x.core.ui.components.input.PrimaryTextField
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ic_mrx
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AuthInputForm(
    modifier: Modifier = Modifier,
    value: String,
    title: String,
    isValueValid: Boolean,
    isButtonEnabled: Boolean,
    errorMessage: String,
    buttonText: String,
    inputLabel: String,
    keyboardType: KeyboardType,
    onValueChanged: (String) -> Unit,
    onButtonClicked: () -> Unit,
) {
    ColumnWithMrxIcon(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        PrimaryTextField(
            value = value,
            onValueChanged = onValueChanged,
            isValueValid = isValueValid,
            label = inputLabel,
            keyboardType = keyboardType,
            imeAction = ImeAction.Send,
            error = errorMessage,
            showKeyboard = true,
            onSend = { onButtonClicked() },
        )
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            onClick = onButtonClicked,
            enabled = isButtonEnabled,
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
            modifier = Modifier
                .padding(top = 100.dp, bottom = 16.dp)
                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
            imageVector = vectorResource(Res.drawable.ic_mrx),
            contentDescription = null
        )
        content()
    }
}
