package com.palkesz.mr.x.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import mrx.composeapp.generated.resources.spy
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
