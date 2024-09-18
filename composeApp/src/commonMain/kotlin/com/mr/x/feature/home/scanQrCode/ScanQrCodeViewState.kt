package com.mr.x.feature.home.scanQrCode

data class ScanQrCodeViewState(
	val event: QrCodeScannedEvent? = null
)

sealed interface QrCodeScannedEvent {
	data class QrCodeScanned(val gameUrl: String) : QrCodeScannedEvent
}