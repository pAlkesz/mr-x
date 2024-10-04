package com.palkesz.mr.x.feature.app

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.AUTH_TAG
import com.palkesz.mr.x.core.usecase.auth.SignInWithLinkUseCase
import com.palkesz.mr.x.core.usecase.game.JoinGameWithGameIdUseCase
import com.palkesz.mr.x.feature.network.NetworkErrorRepository
import dev.theolm.rinku.DeepLink
import dev.theolm.rinku.getParameter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer

interface AppViewModel {
    val viewState: StateFlow<AppViewState>

    fun onDeepLinkReceived(link: DeepLink)
    fun onEventHandled()
}

@Stable
class AppViewModelImpl(
    private val networkErrorRepository: NetworkErrorRepository,
    private val authRepository: AuthRepository,
    private val singInWithLinkUseCase: SignInWithLinkUseCase,
    private val joinGameWithGameIdUseCase: JoinGameWithGameIdUseCase,
) : ViewModel(), AppViewModel {

    private val _viewState = MutableStateFlow(AppViewState(isLoggedIn = authRepository.isLoggedIn))
    override val viewState = _viewState.asStateFlow()

    init {
        observeErrorChanges()
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

    override fun onEventHandled() {
        _viewState.update { it.copy(event = null) }
    }

    override fun onDeepLinkReceived(link: DeepLink) {
        if (authRepository.isSignInLink(link = link)) {
            handleSignInLink(link = link)
        } else {
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
    }

    private fun handleSignInLink(link: DeepLink) {
        if (authRepository.isLoggedIn) {
            return
        }
        viewModelScope.launch {
            singInWithLinkUseCase.run(link = link).onSuccess {
                _viewState.update { state ->
                    state.copy(
                        event = if (authRepository.username == null) {
                            AppEvent.NavigateToAddUsername
                        } else {
                            AppEvent.NavigateToHome
                        }
                    )
                }
            }.also {
                Napier.d(tag = AUTH_TAG) { "Signing in with link: $it" }
            }
        }
    }

    companion object {
        const val GAME_PARAM = "game"
        const val MRX_APP = "mrxapp"
    }
}
