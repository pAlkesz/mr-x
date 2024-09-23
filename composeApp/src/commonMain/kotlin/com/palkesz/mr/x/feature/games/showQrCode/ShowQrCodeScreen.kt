package com.palkesz.mr.x.feature.games.showQrCode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.palkesz.mr.x.core.ui.components.AnimatedNullability
import com.palkesz.mr.x.di.koinViewModel
import com.palkesz.mr.x.feature.app.LocalAppState
import mrx.composeapp.generated.resources.Res
import mrx.composeapp.generated.resources.share_link
import mrx.composeapp.generated.resources.show_qr_code
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import qrgenerator.QRCodeImage

@Composable
fun ShowQrCodeScreen(
	viewModel: ShowQrCodeViewModel = koinViewModel<ShowQrCodeViewModelImpl>(),
	gameId: String
) {
	val viewState by viewModel.viewState.collectAsState()

	ShowQrCodeScreenContent(
		viewState = viewState,
		gameId = gameId,
		setGameId = viewModel::setGameId,
		onShareButtonClicked = viewModel::onShareButtonClicked,
	)
}

@Composable
fun ShowQrCodeScreenContent(
	viewState: ShowQrCodeViewState,
	gameId: String,
	setGameId: (String) -> Unit,
	onShareButtonClicked: () -> Unit,
) {
	val appState = LocalAppState.current
	LaunchedEffect(Unit) {
		setGameId(gameId)
		appState.apply {
			showTopAppBar()
			hideBottomAppBar()
			setScreenTitle(getString(Res.string.show_qr_code))
		}
	}

	AnimatedNullability(viewState.gameUrl) { url ->
		Column(
			modifier = Modifier.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.SpaceAround
		) {
			QRCodeImage(url = url, contentDescription = null)
			OutlinedButton(onClick = onShareButtonClicked) {
				Row {
					Text(text = stringResource(Res.string.share_link))
					Icon(Icons.Default.Share, null)
				}
			}
		}
	}
}
