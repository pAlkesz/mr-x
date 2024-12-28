package com.palkesz.mr.x.feature.home.join

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.ui.effects.TitleBarEffect
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.app.appbars.titlebarstate.TitleBarDetails
import com.palkesz.mr.x.feature.games.GameGraph
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.join_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import qrscanner.CameraLens
import qrscanner.QrScanner

@Composable
fun JoinGameScreen(viewModel: JoinGameViewModel = koinViewModel<JoinGameViewModelImpl>()) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    JoinGameScreenContent(
        viewState = viewState,
        onQrCodeScanned = viewModel::onQrCodeScanned,
        onRetry = viewModel::onRetry,
        onEventHandled = viewModel::onEventHandled,
    )
}

@Composable
private fun JoinGameScreenContent(
    viewState: ViewState<JoinGameViewState>,
    onQrCodeScanned: (String) -> Unit,
    onRetry: () -> Unit,
    onEventHandled: () -> Unit,
) {
    TitleBarEffect(
        details = TitleBarDetails.CenteredTitleBarDetails(
            title = stringResource(Res.string.join_screen_title)
        )
    )
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onRetry) { state ->
        HandleEvent(event = state.event, onEventHandled = onEventHandled)
        QrScanner(
            modifier = Modifier,
            cameraLens = CameraLens.Back,
            onCompletion = onQrCodeScanned,
            flashlightOn = false,
            openImagePicker = false,
            onFailure = {},
            imagePickerHandler = {},
            overlayBorderColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun HandleEvent(event: JoinGameEvent?, onEventHandled: () -> Unit) {
    HandleEventEffect(event) { joinGameEvent, _, _, navController ->
        when (joinGameEvent) {
            is JoinGameEvent.NavigateToGames -> {
                navController?.navigate(route = GameGraph.Games(joinedGameId = joinGameEvent.gameId))
            }
        }
        onEventHandled()
    }
}
