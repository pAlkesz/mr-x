package com.palkesz.mr.x.core.ui.components.animation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.app.appbars.titlebarstate.TitleBarDetails

@Composable
fun CrossFade(
    condition: Boolean,
    onConditionTrue: @Composable () -> Unit,
    onConditionFalse: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Crossfade(targetState = condition, modifier = modifier) {
        if (it) {
            onConditionTrue()
        } else {
            onConditionFalse()
        }
    }
}

@Composable
fun CrossFade(
    titleBarDetails: TitleBarDetails?,
    centeredTitleBar: @Composable (TitleBarDetails.CenteredTitleBarDetails) -> Unit,
    gameTitleBar: @Composable (TitleBarDetails.GameTitleBarDetails) -> Unit,
) {
    CrossFade(
        targetState = titleBarDetails,
        contentKey = { details ->
            details?.let { it::class.simpleName }
        },
    ) { details ->
        when (details) {
            is TitleBarDetails.CenteredTitleBarDetails -> {
                centeredTitleBar(details)
            }

            is TitleBarDetails.GameTitleBarDetails -> {
                gameTitleBar(details)
            }

            else -> {
                Spacer(modifier = Modifier.windowInsetsPadding(insets = TopAppBarDefaults.windowInsets))
            }
        }
    }
}

@Composable
fun <T> CrossFade(
    state: ViewState<T>,
    onLoading: @Composable () -> Unit,
    onSuccess: @Composable (T) -> Unit,
    onError: @Composable () -> Unit,
) {
    CrossFade(
        targetState = state,
        contentKey = { it::class.simpleName + it.isLoading.toString() },
    ) { viewState ->
        when {
            viewState.isLoading -> {
                onLoading()
            }

            viewState is ViewState.Failure -> {
                onError()
            }

            viewState is ViewState.Success -> {
                onSuccess(viewState.data)
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
