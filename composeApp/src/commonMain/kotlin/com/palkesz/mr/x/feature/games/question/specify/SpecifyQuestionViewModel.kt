package com.palkesz.mr.x.feature.games.question.specify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.util.extensions.capitalizeWords
import com.palkesz.mr.x.core.util.extensions.combine
import com.palkesz.mr.x.core.util.extensions.getName
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

interface SpecifyQuestionViewModel {
    val viewState: StateFlow<ViewState<SpecifyQuestionViewState>>

    fun onTextChanged(text: String)
    fun onSaveClicked()
    fun onEventHandled()
}

class SpecifyQuestionViewModelImpl(
    private val gameId: String,
    private val questionId: String,
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository,
    gameRepository: GameRepository,
    konnectivity: Konnectivity,
) : ViewModel(), SpecifyQuestionViewModel {

    private val dataLoader = DataLoader<Unit>()

    private val refreshTrigger = RefreshTrigger()

    private val loadingResult = dataLoader.loadAndObserveDataAsState(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = ViewState.Success(Unit),
        fetchData = { specifyQuestion() },
    )

    private val questionText = MutableStateFlow("")

    private val isQuestionTextValid = MutableStateFlow(true)

    private val event = MutableStateFlow<SpecifyQuestionEvent?>(null)

    override val viewState = combine(
        loadingResult,
        questionRepository.questions.map { questions ->
            questions.first { it.id == questionId }
        }.distinctUntilChanged(),
        gameRepository.games.map { games ->
            games.find { it.id == gameId }
        }.distinctUntilChanged(),
        questionText,
        isQuestionTextValid,
        event,
        konnectivity.isConnectedState,
    ) { result, question, game, text, isTextValid, event, isConnected ->
        result.map {
            SpecifyQuestionViewState(
                text = text,
                oldText = question.text,
                hostAnswer = question.hostAnswer?.getName()?.capitalizeWords().orEmpty(),
                expectedAnswer = "${question.expectedFirstName} ${question.expectedLastName}".capitalizeWords(),
                number = question.number,
                owner = userRepository.users.value.first { it.id == question.userId }.name,
                isTextValid = isTextValid,
                isUpdateButtonEnabled = isConnected && game?.status == GameStatus.ONGOING,
                event = event,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ViewState.Loading)

    override fun onTextChanged(text: String) {
        questionText.update { text }
        isQuestionTextValid.update { true }
    }

    override fun onSaveClicked() {
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }

    override fun onEventHandled() {
        event.update { null }
    }

    private suspend fun specifyQuestion(): Result<Unit> {
        val isTextValid =
            questionText.value.isNotBlank() && questionRepository.questions.value.find {
                it.id == questionId
            }?.text?.equals(questionText.value, ignoreCase = true) == false
        isQuestionTextValid.update { isTextValid }
        return if (isTextValid) {
            questionRepository.updateText(id = questionId, text = questionText.value).onSuccess {
                event.update { SpecifyQuestionEvent.NavigateUp }
            }
        } else {
            Result.success(Unit)
        }
    }
}
