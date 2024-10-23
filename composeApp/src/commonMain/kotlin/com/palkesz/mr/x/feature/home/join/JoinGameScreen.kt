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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.effects.HandleEventEffect
import com.palkesz.mr.x.feature.home.HomeGraphRoute
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
        onEventHandled = viewModel::onEventHandled,
    )
}

@Composable
private fun JoinGameScreenContent(
    viewState: JoinGameViewState,
    onQrCodeScanned: (String) -> Unit,
    onBackPressed: () -> Unit,
    onEventHandled: () -> Unit,
) {
    HandleEvent(event = viewState.event, onEventHandled = onEventHandled)
    Box(modifier = Modifier.fillMaxSize()) {
        val scannerColor = MaterialTheme.colorScheme.primaryContainer
        QrScanner(
            modifier = Modifier,
            cameraLens = CameraLens.Back,
            onCompletion = onQrCodeScanned,
            flashlightOn = false,
            openImagePicker = false,
            onFailure = {},
            imagePickerHandler = {},
            customOverlay = { customOverlay(scannerColor) }
        )
        FloatingActionButton(
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            onClick = onBackPressed
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
        }
    }
}

@Composable
private fun HandleEvent(event: JoinGameEvent?, onEventHandled: () -> Unit) {
    HandleEventEffect(event) { joinGameEvent, _, _, navController ->
        when (joinGameEvent) {
            is JoinGameEvent.QrCodeScanned -> {
                //TODO
            }

            is JoinGameEvent.NavigateToHome -> {
                navController?.navigate(HomeGraphRoute.HomePage.route)
            }
        }
        onEventHandled()
    }
}

private fun ContentDrawScope.customOverlay(borderColor: Color) {
    drawContent()
    val scanAreaSize = size.width * 0.65f
    val left = (size.width - scanAreaSize) / 2
    val top = (size.height - scanAreaSize) / 2
    drawRoundRect(
        color = borderColor,
        cornerRadius = CornerRadius(x = 10.dp.toPx()),
        topLeft = Offset(left, top),
        size = Size(scanAreaSize, scanAreaSize),
        style = Stroke(width = 6.dp.toPx()),
    )
}
