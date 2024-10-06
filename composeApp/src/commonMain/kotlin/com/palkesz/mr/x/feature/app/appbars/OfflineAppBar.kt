package com.palkesz.mr.x.feature.app.appbars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.offline_bar_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun OfflineAppBar(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(vertical = 4.dp),
        text = stringResource(Res.string.offline_bar_label),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSecondary,
        style = MaterialTheme.typography.bodyMedium,
    )
}
