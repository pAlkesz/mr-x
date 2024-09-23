package com.palkesz.mr.x.feature.games.showQrCode

import androidx.lifecycle.ViewModel
import com.palkesz.mr.x.core.util.createUniversalAppLinkFromGameId
import dev.theolm.rinku.Rinku
import dev.theolm.rinku.models.DeepLinkParam
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.builtins.serializer

interface ShowQrCodeViewModel {
	val viewState: StateFlow<ShowQrCodeViewState>

	fun setGameId(uuid: String)
	fun onShareButtonClicked()
}

class ShowQrCodeViewModelImpl(
	private val showShareSheetHelper: ShowShareSheetHelper
) : ViewModel(), ShowQrCodeViewModel {

	private val _viewState = MutableStateFlow(ShowQrCodeViewState())
	override val viewState = _viewState.asStateFlow()

	override fun setGameId(uuid: String) {
		_viewState.update { state ->
			state.copy(
				gameUrl = Rinku.buildUrl(
					DOMAIN,
					DeepLinkParam(GAME_PARAM, uuid, String.serializer())),
				gameId = uuid)
		}
	}

	override fun onShareButtonClicked() {
		_viewState.value.gameId?.let {
			val link = createUniversalAppLinkFromGameId(gameId = it)
			showShareSheetHelper.showShareSheet(link)
		}
	}

	companion object {
		const val DOMAIN = "mrxapp://"
		const val GAME_PARAM = "game"
	}
}