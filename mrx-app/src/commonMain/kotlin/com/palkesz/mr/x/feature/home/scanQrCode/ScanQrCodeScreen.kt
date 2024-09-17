package com.palkesz.mr.x.feature.home.scanQrCode

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.palkesz.mr.x.core.ui.theme.MrXTheme
import com.palkesz.mr.x.core.ui.theme.grayColor
import com.palkesz.mr.x.di.koinViewModel
import com.palkesz.mr.x.feature.app.LocalAppScope
import com.palkesz.mr.x.feature.app.LocalAppState
import com.palkesz.mr.x.feature.app.LocalSnackBarHostState
import dev.theolm.rinku.Rinku
import kotlinx.coroutines.launch
import mrx.mrx_app.generated.resources.Res
import mrx.mrx_app.generated.resources.joining_in_progress
import mrx.mrx_app.generated.resources.scan_qr_code
import org.jetbrains.compose.resources.getString
import qrscanner.QrScanner

@Composable
fun ScanQrCodeScreen(viewModel: ScanQrCodeViewModel = koinViewModel<ScanQrCodeViewModelImpl>()) {
	val viewState by viewModel.viewState.collectAsState()

	ScanQrCodeScreenContent(
		viewState = viewState,
		onCompletion = viewModel::onQrCodeScanned,
		onEventHandled = viewModel::onEventHandled
	)
}

@Composable
fun ScanQrCodeScreenContent(
	viewState: ScanQrCodeViewState,
	onCompletion: (String) -> Unit,
	onEventHandled: () -> Unit
) {
	val appState = LocalAppState.current

	LaunchedEffect(Unit) {
		appState.apply {
			showTopAppBar()
			hideBottomAppBar()
			setScreenTitle(getString(Res.string.scan_qr_code))
		}
	}

	HandleEvent(
		onEventHandled = onEventHandled,
		event = viewState.event
	)

	Box(
		modifier = Modifier
			.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		QrScanner(
			modifier = Modifier
				.clipToBounds(),
			onCompletion = onCompletion,
			flashlightOn = false,
			launchGallery = false,
			onFailure = {},
			onGalleryCallBackHandler = {}
		)
		TransparentClipLayout(
			modifier = Modifier.fillMaxSize(),
			width = 300.dp,
			height = 300.dp,
			offsetY = 150.dp,
			color = MrXTheme.extraColors.qrCodeColor
		)
	}
}

@Composable
fun TransparentClipLayout(
	modifier: Modifier,
	width: Dp,
	height: Dp,
	offsetY: Dp,
	color: Color
) {
	val offsetInPx: Float
	val widthInPx: Float
	val heightInPx: Float

	with(LocalDensity.current) {
		offsetInPx = offsetY.toPx()
		widthInPx = width.toPx()
		heightInPx = height.toPx()
	}

	Canvas(modifier = modifier) {
		val canvasWidth = size.width

		with(drawContext.canvas.nativeCanvas) {
			val checkPoint = saveLayer(null, null)

			drawRect(color)

			drawRoundRect(
				topLeft = Offset(
					x = (canvasWidth - widthInPx) / 2,
					y = offsetInPx
				),
				size = Size(widthInPx, heightInPx),
				cornerRadius = CornerRadius(30f, 30f),
				color = Color.Transparent,
				blendMode = BlendMode.Clear
			)
			restoreToCount(checkPoint)
		}
	}
}

@Composable
fun HandleEvent(
	onEventHandled: () -> Unit,
	event: QrCodeScannedEvent?
) {
	val snackbarHostState = LocalSnackBarHostState.current

	when (event) {
		is QrCodeScannedEvent.QrCodeScanned -> {
			LocalAppScope.current?.launch {
				snackbarHostState.showSnackbar(message = getString(Res.string.joining_in_progress))
			}
			Rinku.handleDeepLink(event.gameUrl)
			onEventHandled()
		}
		null -> return
	}
}