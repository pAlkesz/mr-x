package com.palkesz.mr.x.feature.home.join

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface JoinGameViewModel {
    val viewState: StateFlow<JoinGameViewState>

    fun onQrCodeScanned(url: String)
    fun onBackPressed()
    fun onEventHandled()
}

@Stable
class JoinGameViewModelImpl : ViewModel(), JoinGameViewModel {

    private val _viewState = MutableStateFlow(JoinGameViewState())
    override val viewState = _viewState.asStateFlow()

    override fun onQrCodeScanned(url: String) {
        //TODO
        /*_viewState.update { state ->
            //state.copy(event = QrCodeScannedEvent.QrCodeScanned(gameUrl))
        }*/
    }

    override fun onBackPressed() {
        _viewState.update { it.copy(event = JoinGameEvent.NavigateToHome) }
    }

    override fun onEventHandled() {
        _viewState.update { it.copy(event = null) }
    }
}
