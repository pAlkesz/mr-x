package com.palkesz.mr.x.feature.games.qrcode

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface QrCodeViewModel {
    val viewState: StateFlow<QrCodeViewState>

    fun onShareButtonClicked()
}

class QrCodeViewModelImpl(
    gameId: String,
    private val shareSheetHelper: ShareSheetHelper,
) : ViewModel(), QrCodeViewModel {

    private val url = "$BASE_URL$gameId"

    private val _viewState = MutableStateFlow(QrCodeViewState(url = url))
    override val viewState = _viewState.asStateFlow()

    override fun onShareButtonClicked() {
        shareSheetHelper.showShareSheet(url = url)
    }

    companion object {
        private const val BASE_URL = "https://mr-x-26c68.firebaseapp.com/?game_id="
    }
}
