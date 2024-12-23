package com.palkesz.mr.x.feature.home.join

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.components.loadingindicator.ContentWithBackgroundLoadingIndicator
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.feature.games.GameGraph
import com.palkesz.mr.x.feature.home.HomeGraph
import org.koin.compose.viewmodel.koinViewModel
import qrscanner.CameraLens
import qrscanner.QrScanner

@Composable
fun JoinGameScreen(viewModel: JoinGameViewModel = koinViewModel<JoinGameViewModelImpl>()) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    JoinGameScreenContent(
        viewState = viewState,
        onQrCodeScanned = viewModel::onQrCodeScanned,
        onBackPressed = viewModel::onBackPressed,
        onRetry = viewModel::onRetry,
        onEventHandled = viewModel::onEventHandled,
    )
}

@Composable
private fun JoinGameScreenContent(
    viewState: ViewState<JoinGameViewState>,
    onQrCodeScanned: (String) -> Unit,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    onEventHandled: () -> Unit,
) {
    ContentWithBackgroundLoadingIndicator(state = viewState, onRetry = onRetry) { state ->
        HandleEvent(event = state.event, onEventHandled = onEventHandled)
        Box(modifier = Modifier.fillMaxSize()) {
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
            FloatingActionButton(
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                onClick = onBackPressed,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    }
}

@Composable
private fun HandleEvent(event: JoinGameEvent?, onEventHandled: () -> Unit) {
    HandleEventEffect(event) { joinGameEvent, _, _, navController ->
        when (joinGameEvent) {
            is JoinGameEvent.NavigateToGames -> {
                navController?.navigate(route = GameGraph.Games(joinedGameId = joinGameEvent.gameId))
            }

            is JoinGameEvent.NavigateToHome -> {
                navController?.navigate(route = HomeGraph.Home)
            }
        }
        onEventHandled()
    }
}
