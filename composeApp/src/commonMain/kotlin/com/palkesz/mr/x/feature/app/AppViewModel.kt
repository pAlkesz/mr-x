package com.palkesz.mr.x.feature.app

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.crashkios.crashlytics.enableCrashlytics
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.crashlytics.Crashlytics
import com.palkesz.mr.x.core.data.datastore.MrxDataStore
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.usecase.game.JoinGameUseCase
import com.palkesz.mr.x.core.util.BUSINESS_TAG
import com.palkesz.mr.x.core.util.platform.isDebug
import com.palkesz.mr.x.feature.app.notifications.NotificationHelper
import com.plusmobileapps.konnectivity.Konnectivity
import dev.theolm.rinku.DeepLink
import dev.theolm.rinku.listenForDeepLinks
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface AppViewModel {
    val viewState: StateFlow<AppViewState>

    fun onEventHandled()
}

@Stable
class AppViewModelImpl(
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository,
    private val questionRepository: QuestionRepository,
    private val barkochbaQuestionRepository: BarkochbaQuestionRepository,
    private val userRepository: UserRepository,
    private val joinGameUseCase: JoinGameUseCase,
    private val konnectivity: Konnectivity,
    private val notificationHelper: NotificationHelper,
    private val mrxDataStore: MrxDataStore,
    crashlytics: Crashlytics,
) : ViewModel(), AppViewModel {

    private val _viewState = MutableStateFlow(
        AppViewState(
            isLoggedIn = authRepository.isLoggedIn,
            isOfflineBarVisible = !konnectivity.isConnected,
            gameNotificationCount = null,
        )
    )
    override val viewState = _viewState.asStateFlow()

    init {
        observeConnectivity()
        observeDeepLinks()
        observeGames()
        observeQuestions()
        observeBarkochbaQuestions()
        observeUsers()
        observeNotifications()
        Napier.d(tag = BUSINESS_TAG) { "Crashlytics enabled: ${!isDebug}" }
        if (!isDebug) {
            crashlytics.apply {
                setCrashlyticsCollectionEnabled(enabled = true)
                enableCrashlytics()
                authRepository.userId?.let {
                    setUserId(userId = it)
                    Napier.d(tag = BUSINESS_TAG) { "Crashlytics user_id set: $it" }
                }
            }
        }
    }

    override fun onEventHandled() {
        _viewState.update { it.copy(event = null) }
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

    private fun observeGames() {
        viewModelScope.launch {
            authRepository.loggedIn.collectLatest { isLoggedIn ->
                if (isLoggedIn) {
                    gameRepository.observeGames()
                }
            }
        }
    }

    private fun observeQuestions() {
        viewModelScope.launch {
            authRepository.loggedIn.collectLatest { isLoggedIn ->
                if (isLoggedIn) {
                    questionRepository.observeQuestions()
                }
            }
        }
    }

    private fun observeBarkochbaQuestions() {
        viewModelScope.launch {
            authRepository.loggedIn.collectLatest { isLoggedIn ->
                if (isLoggedIn) {
                    barkochbaQuestionRepository.observeQuestions()
                }
            }
        }
    }

    private fun observeUsers() {
        viewModelScope.launch {
            authRepository.loggedIn.collectLatest { isLoggedIn ->
                if (isLoggedIn) {
                    userRepository.observeUsers()
                }
            }
        }
    }

    private fun observeNotifications() {
        viewModelScope.launch {
            notificationHelper.event.collect { event ->
                _viewState.update { it.copy(event = event) }
            }
        }
        viewModelScope.launch {
            gameRepository.games.flatMapLatest { games ->
                mrxDataStore.observeNotificationCount(gameIds = games.map { it.id })
            }.collect { notifications ->
                _viewState.update { state ->
                    state.copy(gameNotificationCount = notifications.sumOf { (_, count) ->
                        count
                    }.takeIf { badgeCount -> badgeCount > 0 })
                }
            }
        }
    }

    private fun onDeepLinkReceived(link: DeepLink) {
        if (authRepository.isSignInLink(link = link)) {
            return
        }
        val gameId = link.parameters[GAME_ID_PARAM_NAME] ?: return
        Napier.d(tag = BUSINESS_TAG) { "Join game link received with game id: $gameId" }
        viewModelScope.launch {
            joinGameUseCase.run(gameId = gameId).onSuccess {
                _viewState.update {
                    it.copy(event = AppEvent.NavigateToGames(gameId))
                }
            }
        }
    }

    companion object {
        private const val GAME_ID_PARAM_NAME = "game_id"
    }
}
