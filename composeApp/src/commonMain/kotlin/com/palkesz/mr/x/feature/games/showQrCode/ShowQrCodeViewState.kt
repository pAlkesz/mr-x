package com.palkesz.mr.x.feature.games.showQrCode

import androidx.compose.runtime.Immutable

@Immutable
data class ShowQrCodeViewState(
	val gameUrl: String? = null,
	val gameId: String? = null,
)
