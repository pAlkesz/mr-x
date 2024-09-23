package com.palkesz.mr.x.feature.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.usecase.game.JoinGameWithGameIdUseCase
import com.palkesz.mr.x.feature.network.NetworkErrorRepository
import dev.theolm.rinku.DeepLink
import dev.theolm.rinku.getParameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer

class AppViewModel(
    private val networkErrorRepository: NetworkErrorRepository,
    private val joinGameWithGameIdUseCase: JoinGameWithGameIdUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(AppViewState())
    val viewState = _viewState.asStateFlow()

    init {
        observeErrorChanges()
    }

    private fun observeErrorChanges() {
        viewModelScope.launch {
            networkErrorRepository.error.collect { error ->
                error.message?.let { message ->
                    _viewState.update {
                        it.copy(event = AppEvent.ErrorOccurred(message = message))
                    }
                }
            }
        }
    }

    fun onEventHandled() {
        _viewState.update {
            it.copy(event = null)
        }
    }

    fun onDeepLinkReceived(deepLink: DeepLink) {
        val gameId = if (deepLink.schema == MRX_APP) {
            deepLink.getParameter(
                GAME_PARAM,
                String.serializer()
            )
        } else {
            deepLink.pathSegments.last()
        }
        joinGameWithGameIdUseCase.run(gameId)
        _viewState.update {
            it.copy(event = AppEvent.DeepLinkReceived(gameId))
        }
    }

    companion object {
        const val GAME_PARAM = "game"
        const val MRX_APP = "mrxapp"
    }
}
