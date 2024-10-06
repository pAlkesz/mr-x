package com.palkesz.mr.x.feature.app

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.usecase.game.JoinGameWithGameIdUseCase
import com.palkesz.mr.x.feature.network.NetworkErrorRepository
import com.plusmobileapps.konnectivity.Konnectivity
import dev.theolm.rinku.DeepLink
import dev.theolm.rinku.getParameter
import dev.theolm.rinku.listenForDeepLinks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer

interface AppViewModel {
    val viewState: StateFlow<AppViewState>

    fun onEventHandled()
}

@Stable
class AppViewModelImpl(
    private val networkErrorRepository: NetworkErrorRepository,
    private val authRepository: AuthRepository,
    private val joinGameWithGameIdUseCase: JoinGameWithGameIdUseCase,
    private val konnectivity: Konnectivity,
) : ViewModel(), AppViewModel {

    private val _viewState = MutableStateFlow(
        AppViewState(
            isLoggedIn = authRepository.isLoggedIn,
            isOfflineBarVisible = !konnectivity.isConnected,
        )
    )
    override val viewState = _viewState.asStateFlow()

    init {
        observeErrorChanges()
        observeConnectivity()
        observeDeepLinks()
    }

    override fun onEventHandled() {
        _viewState.update { it.copy(event = null) }
    }

    private fun observeErrorChanges() {
        viewModelScope.launch {
            networkErrorRepository.error.collect { error ->
                error.message?.let { message ->
                    _viewState.update { it.copy(event = AppEvent.ShowSnackbar(message = message)) }
                }
            }
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            konnectivity.isConnectedState.collect { isConnected ->
                _viewState.update { it.copy(isOfflineBarVisible = !isConnected) }
            }
        }
    }

    private fun observeDeepLinks() {
        viewModelScope.launch {
            authRepository.loggedIn.collectLatest { isLoggedIn ->
                if (isLoggedIn) {
                    listenForDeepLinks { link ->
                        onDeepLinkReceived(link = link)
                    }
                }
            }
        }
    }

    private fun onDeepLinkReceived(link: DeepLink) {
        if (authRepository.isSignInLink(link = link)) {
            return
        }
        val gameId =
            if (link.schema == MRX_APP) {
                link.getParameter(
                    GAME_PARAM,
                    String.serializer(),
                )
            } else {
                link.pathSegments.last()
            }
        joinGameWithGameIdUseCase.run(gameId)
        _viewState.update {
            it.copy(event = AppEvent.NavigateToMyGames(gameId))
        }
    }

    companion object {
        const val GAME_PARAM = "game"
        const val MRX_APP = "mrxapp"
    }
}
