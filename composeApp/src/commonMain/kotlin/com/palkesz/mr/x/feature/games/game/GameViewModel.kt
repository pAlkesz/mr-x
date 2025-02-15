package com.palkesz.mr.x.feature.games.game

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.datastore.MrxDataStore
import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.usecase.game.FetchGameResultUseCase
import com.palkesz.mr.x.core.usecase.game.ObserveGameResultUseCase
import com.palkesz.mr.x.core.util.networking.DataLoader
import com.palkesz.mr.x.core.util.networking.RefreshTrigger
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.map
import com.palkesz.mr.x.feature.app.notifications.NotificationFilter
import com.palkesz.mr.x.feature.app.notifications.NotificationHelper
import com.palkesz.mr.x.proto.LocalNotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface GameViewModel {
    val viewState: StateFlow<ViewState<GameViewState>>

    fun onGuessAsHostClicked(questionId: String)
    fun onPassAsHostClicked(questionId: String)
    fun onGuessAsPlayerClicked(questionId: String)
    fun onAcceptAsOwnerClicked(questionId: String)
    fun onDeclineAsOwnerClicked(questionId: String)
    fun onAskQuestionClicked()
    fun onAskBarkochbaQuestionClicked()
    fun onBarkochbaQuestionAnswered(questionId: String, answer: Boolean)
    fun onQrCodeClicked()
    fun onPageSelected(page: Int)
    fun onLeavingScreen()
    fun onEventHandled()
    fun onRetry()
}

@Stable
class GameViewModelImpl(
    private val gameId: String,
    tabIndex: Int,
    private val addedQuestionId: String?,
    private val addedBarkochbaQuestionId: String?,
    private val fetchGameResultUseCase: FetchGameResultUseCase,
    private val observeGameResultUseCase: ObserveGameResultUseCase,
    private val gameUiMapper: GameUiMapper,
    private val questionRepository: QuestionRepository,
    private val barkochbaQuestionRepository: BarkochbaQuestionRepository,
    private val notificationHelper: NotificationHelper,
    mrxDataStore: MrxDataStore,
) : ViewModel(), GameViewModel {

    private val dataLoader = DataLoader<GameResult>()

    private val refreshTrigger = RefreshTrigger()

    private val loadingResult = dataLoader.loadAndObserveDataAsState(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = ViewState.Loading,
        fetchData = { fetchGameResultUseCase.run(id = gameId) },
        observeData = { observeGameResultUseCase.run(id = gameId) },
    )

    private val event = MutableStateFlow<GameEvent?>(value = GameEvent.GoToTab(index = tabIndex))

    override val viewState = combine(
        loadingResult,
        mrxDataStore.observeNotificationCount(gameId = gameId),
        event,
    ) { result, notificationCount, event ->
        result.map { gameResult ->
            gameUiMapper.mapViewState(
                result = gameResult,
                notificationCount = notificationCount,
                addedQuestionId = addedQuestionId,
                addedBarkochbaQuestionId = addedBarkochbaQuestionId,
                event = event,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ViewState.Loading)

    override fun onAcceptAsOwnerClicked(questionId: String) {
        event.update {
            GameEvent.NavigateToSpecifyQuestion(gameId = gameId, questionId = questionId)
        }
    }

    override fun onQrCodeClicked() {
        event.update { GameEvent.NavigateToQrCode(gameId = gameId) }
    }

    override fun onDeclineAsOwnerClicked(questionId: String) {
        viewModelScope.launch {
            questionRepository.updateStatus(
                id = questionId,
                status = QuestionStatus.WAITING_FOR_PLAYERS,
            )
        }
    }

    override fun onGuessAsHostClicked(questionId: String) {
        event.update { GameEvent.NavigateToGuessQuestion(gameId = gameId, questionId = questionId) }
    }

    override fun onGuessAsPlayerClicked(questionId: String) {
        event.update { GameEvent.NavigateToGuessQuestion(gameId = gameId, questionId = questionId) }
    }

    override fun onPassAsHostClicked(questionId: String) {
        viewModelScope.launch {
            questionRepository.updateStatus(
                id = questionId,
                status = QuestionStatus.WAITING_FOR_PLAYERS,
            )
        }
    }

    override fun onAskQuestionClicked() {
        event.update { GameEvent.NavigateToCreateQuestion(gameId = gameId) }
    }

    override fun onAskBarkochbaQuestionClicked() {
        event.update { GameEvent.NavigateToCreateBarkochbaQuestion(gameId = gameId) }
    }

    override fun onBarkochbaQuestionAnswered(questionId: String, answer: Boolean) {
        viewModelScope.launch {
            barkochbaQuestionRepository.updateAnswer(id = questionId, answer = answer)
        }
    }

    override fun onPageSelected(page: Int) {
        notificationHelper.setNotificationFilter(
            filter = NotificationFilter(
                gameId = gameId,
                type = if (page == 0) LocalNotificationType.QUESTION else LocalNotificationType.BARKOCHBA,
            )
        )
    }

    override fun onLeavingScreen() {
        notificationHelper.setNotificationFilter(
            filter = NotificationFilter(
                gameId = null,
                type = LocalNotificationType.QUESTION,
            )
        )
    }

    override fun onRetry() {
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }

    override fun onEventHandled() {
        event.update { null }
    }

}
