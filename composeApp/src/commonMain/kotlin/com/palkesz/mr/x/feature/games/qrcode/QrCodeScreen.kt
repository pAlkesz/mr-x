package com.palkesz.mr.x.feature.games.qrcode

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.palkesz.mr.x.core.ui.effects.TitleBarEffect
import com.palkesz.mr.x.feature.app.appbars.titlebarstate.TitleBarDetails
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.qrcode_screen_title
import mrx.composeapp.generated.resources.share_button_label
import org.jetbrains.compose.resources.stringResource
import qrgenerator.qrkitpainter.QrKitBallShape
import qrgenerator.qrkitpainter.QrKitBrush
import qrgenerator.qrkitpainter.QrKitColors
import qrgenerator.qrkitpainter.QrKitFrameShape
import qrgenerator.qrkitpainter.QrKitPixelShape
import qrgenerator.qrkitpainter.QrKitShapes
import qrgenerator.qrkitpainter.createCircle
import qrgenerator.qrkitpainter.createRoundCorners
import qrgenerator.qrkitpainter.fromPixelShape
import qrgenerator.qrkitpainter.rememberQrKitPainter
import qrgenerator.qrkitpainter.solidBrush

@Composable
fun QrCodeScreen(viewModel: QrCodeViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    QrCodeScreenContent(
        viewState = viewState,
        onShareButtonClicked = viewModel::onShareButtonClicked,
    )
}

@Composable
private fun QrCodeScreenContent(viewState: QrCodeViewState, onShareButtonClicked: () -> Unit) {
    TitleBarEffect(
        details = TitleBarDetails.CenteredTitleBarDetails(
            title = stringResource(Res.string.qrcode_screen_title)
        )
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val darkBrushColor = MaterialTheme.colorScheme.onSurface
        val lightBrushColor = MaterialTheme.colorScheme.surface
        val painter = rememberQrKitPainter(
            data = viewState.url,
            qrOptions = {
                colors = QrKitColors(
                    darkBrush = QrKitBrush.solidBrush(color = darkBrushColor),
                    lightBrush = QrKitBrush.solidBrush(color = lightBrushColor)
                )
                shapes = QrKitShapes(
                    frameShape = QrKitFrameShape.createRoundCorners(cornerRadius = 10f),
                    ballShape = QrKitBallShape.fromPixelShape(QrKitPixelShape.createCircle()),
                    darkPixelShape = QrKitPixelShape.createCircle(),
                )
                isFourEyeEnabled = true
            }
        )
        Image(
            modifier = Modifier
                .widthIn(max = 488.dp)
                .fillMaxWidth()
                .padding(start = 64.dp, end = 64.dp, bottom = 32.dp),
            painter = painter,
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
        )
        OutlinedButton(
            onClick = onShareButtonClicked,
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(Res.string.share_button_label),
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp),
                modifier = Modifier.padding(8.dp),
            )
            Icon(Icons.Default.Share, null)
        }
    }
}
