package com.palkesz.mr.x.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.enter_first_name
import mrx.composeapp.generated.resources.enter_last_name
import mrx.composeapp.generated.resources.first_name
import mrx.composeapp.generated.resources.last_name
import mrx.composeapp.generated.resources.spy
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
            autoCorrect = false
        ),
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
