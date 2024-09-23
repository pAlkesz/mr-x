package com.palkesz.mr.x.feature.home.scanQrCode

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface ScanQrCodeViewModel {
	val viewState: StateFlow<ScanQrCodeViewState>
	fun onQrCodeScanned(gameUrl: String)
	fun onEventHandled()
}

class ScanQrCodeViewModelImpl : ViewModel(), ScanQrCodeViewModel {

	private val _viewState = MutableStateFlow(ScanQrCodeViewState())
	override val viewState = _viewState.asStateFlow()

	override fun onQrCodeScanned(gameUrl: String) {
		_viewState.update { state ->
			state.copy(event = QrCodeScannedEvent.QrCodeScanned(gameUrl))
		}
	}

	override fun onEventHandled() {
		_viewState.update { state ->
			state.copy(event = null)
		}
	}
}