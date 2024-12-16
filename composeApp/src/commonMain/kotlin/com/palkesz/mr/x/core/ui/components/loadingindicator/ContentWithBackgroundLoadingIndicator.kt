package com.palkesz.mr.x.core.ui.components.loadingindicator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.components.animation.CrossFade
import com.palkesz.mr.x.core.ui.modifiers.debouncedClickable
import com.palkesz.mr.x.core.util.extensions.isNotNullOrBlank
import com.palkesz.mr.x.core.util.networking.ViewState
import kotlinx.coroutines.delay
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.ic_error
import mrx.composeapp.generated.resources.network_error_label
import mrx.composeapp.generated.resources.retry_button_label
import mrx.composeapp.generated.resources.slow_connection_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun <T> ContentWithBackgroundLoadingIndicator(
    state: ViewState<T>,
    loadingLabel: String? = null,
    errorLabel: String? = null,
    onRetry: () -> Unit,
    content: @Composable (T) -> Unit
) {
    CrossFade(
        state = state,
        onLoading = {
            LoadingIndicator(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                label = loadingLabel,
            )
        },
        onSuccess = content,
        onError = {
            ErrorIndicator(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                label = errorLabel,
                onRetry = onRetry,
            )
        },
    )
}

@Composable
fun <T> ContentWithBackgroundLoadingIndicator(
    state: ViewState<T>,
    onRetry: () -> Unit,
    content: @Composable (T) -> Unit
) {
    CrossFade(
        state = state,
        onLoading = { LoadingIndicator(modifier = Modifier.padding(16.dp).fillMaxSize()) },
        onSuccess = content,
        onError = {
            ErrorIndicator(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                onRetry = onRetry,
            )
        },
    )
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier, label: String? = null) {
    var showSlowConnection by remember { mutableStateOf(value = false) }
    LaunchedEffect(key1 = Unit) {
        delay(DELAY_FOR_BAD_CONNECTION_TEXT)
        showSlowConnection = true
    }
    CenteredColumn(modifier = modifier) {
        CircularProgressIndicator()
        AnimatedVisibility(visible = showSlowConnection || label.isNotNullOrBlank()) {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = label ?: stringResource(Res.string.slow_connection_label),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun ErrorIndicator(
    modifier: Modifier = Modifier,
    label: String? = null,
    onRetry: () -> Unit,
) {
    CenteredColumn(modifier = modifier) {
        Image(
            modifier = Modifier.padding(bottom = 8.dp),
            imageVector = vectorResource(resource = Res.drawable.ic_error),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
            contentDescription = null
        )
        Text(
            text = label ?: stringResource(Res.string.network_error_label),
            style = MaterialTheme.typography.bodyLarge,
        )
        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier.padding(vertical = 8.dp).debouncedClickable { onRetry() },
        ) {
            Text(text = stringResource(Res.string.retry_button_label))
        }
    }
}

@Composable
private fun CenteredColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content,
    )
}

private const val DELAY_FOR_BAD_CONNECTION_TEXT = 5000L
