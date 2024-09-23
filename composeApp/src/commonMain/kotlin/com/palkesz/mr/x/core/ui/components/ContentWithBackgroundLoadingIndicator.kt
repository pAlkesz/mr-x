package com.palkesz.mr.x.core.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.util.networking.ViewState
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.error_screen_button_label
import mrx.composeapp.generated.resources.something_wrong
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> ContentWithBackgroundLoadingIndicator(
    state: ViewState<T>,
    onRetry: () -> Unit,
    content: @Composable (T) -> Unit
) {
    CrossFade(targetState = state, contentKey = { it::class.simpleName }) { viewState ->
        when (viewState) {
            is ViewState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is ViewState.Failure -> {
                ErrorScreen(onRetry)
            }

            is ViewState.Success -> {
                content(viewState.data)
            }
        }
    }
}

@Composable
private fun ErrorScreen(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.something_wrong),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp)
            )
            DebouncedButton(
                onClick = onRetry,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(12.dp),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(
                    text = stringResource(Res.string.error_screen_button_label),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}

@Composable
fun <T> CrossFade(
    targetState: T,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    label: String = "CrossFade",
    contentKey: (targetState: T) -> Any?,
    content: @Composable (T) -> Unit
) {
    val transition = updateTransition(targetState, label)
    transition.Crossfade(modifier, animationSpec, contentKey = contentKey, content = content)
}
