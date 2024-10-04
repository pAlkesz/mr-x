package com.palkesz.mr.x.core.ui.components.button

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.widthIn(max = 488.dp).fillMaxWidth().defaultMinSize(minHeight = 48.dp),
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
