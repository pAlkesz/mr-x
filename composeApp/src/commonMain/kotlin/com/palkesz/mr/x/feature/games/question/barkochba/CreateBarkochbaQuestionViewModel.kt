package com.palkesz.mr.x.feature.games.question.barkochba

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.usecase.question.UpdateBarkochbaQuestionUseCase
import com.palkesz.mr.x.core.util.extensions.combine
import com.palkesz.mr.x.core.util.networking.DataLoader
import com.palkesz.mr.x.core.util.networking.RefreshTrigger
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.map
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface CreateBarkochbaQuestionViewModel {
    val viewState: StateFlow<ViewState<CreateBarkochbaQuestionViewState>>

    fun onTextChanged(text: String)
    fun onCreateClicked()
    fun onEventHandled()
}

@Stable
class CreateBarkochbaQuestionViewModelImpl(
    private val gameId: String,
    private val updateBarkochbaQuestionUseCase: UpdateBarkochbaQuestionUseCase,
    gameRepository: GameRepository,
    konnectivity: Konnectivity,
) : ViewModel(), CreateBarkochbaQuestionViewModel {

    private val dataLoader = DataLoader<Unit>()

    private val refreshTrigger = RefreshTrigger()

    private val loadingResult = dataLoader.loadAndObserveDataAsState(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = ViewState.Success(Unit),
        fetchData = { updateQuestion() },
    )

    private val questionText = MutableStateFlow("")

    private val isQuestionTextValid = MutableStateFlow(true)

    private val event = MutableStateFlow<CreateBarkochbaQuestionEvent?>(null)

    override val viewState = combine(
        loadingResult,
        gameRepository.games.map { games ->
            games.find { it.id == gameId }
        }.distinctUntilChanged(),
        questionText,
        isQuestionTextValid,
        event,
        konnectivity.isConnectedState
    ) { result, game, text, isTextValid, event, isConnected ->
        result.map {
            CreateBarkochbaQuestionViewState(
                text = text,
                isTextValid = isTextValid,
                isCreateButtonEnabled = isConnected && game?.status == GameStatus.ONGOING,
                event = event,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ViewState.Loading)

    override fun onTextChanged(text: String) {
        questionText.update { text }
        isQuestionTextValid.update { true }
    }

    override fun onCreateClicked() {
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }

    override fun onEventHandled() {
        event.update { null }
    }

    private suspend fun updateQuestion(): Result<Unit> {
        val isTextValid = questionText.value.isNotBlank()
        isQuestionTextValid.update { isTextValid }
        return if (isTextValid) {
            updateBarkochbaQuestionUseCase.run(
                gameId = gameId,
                text = questionText.value,
            ).onSuccess { questionId ->
                event.update {
                    CreateBarkochbaQuestionEvent.NavigateUp(
                        gameId = gameId,
                        questionId = questionId,
                    )
                }
            }.map { Unit }
        } else {
            Result.success(Unit)
        }
    }
}
