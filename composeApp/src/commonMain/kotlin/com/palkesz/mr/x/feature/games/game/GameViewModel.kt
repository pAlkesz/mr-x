package com.palkesz.mr.x.feature.games.game

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.usecase.game.FetchGameResultUseCase
import com.palkesz.mr.x.core.usecase.game.ObserveGameResultUseCase
import com.palkesz.mr.x.core.util.networking.DataLoader
import com.palkesz.mr.x.core.util.networking.RefreshTrigger
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.map
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
    fun onQrCodeClicked()
    fun onEventHandled()
    fun onRetry()
}

@Stable
class GameViewModelImpl(
    private val gameId: String,
    private val fetchGameResultUseCase: FetchGameResultUseCase,
    private val observeGameResultUseCase: ObserveGameResultUseCase,
    private val gameUiMapper: GameUiMapper,
    private val questionRepository: QuestionRepository,
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

    private val event = MutableStateFlow<GameEvent?>(null)

    override val viewState = combine(loadingResult, event) { result, event ->
        result.map { gameResult ->
            gameUiMapper.mapViewState(result = gameResult, event = event)
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

    override fun onRetry() {
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }

    override fun onEventHandled() {
        event.update { null }
    }

}
