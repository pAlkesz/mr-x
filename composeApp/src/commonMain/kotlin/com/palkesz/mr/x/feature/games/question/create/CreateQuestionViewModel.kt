package com.palkesz.mr.x.feature.games.question.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.usecase.question.CreateQuestionUseCase
import com.palkesz.mr.x.core.util.extensions.combine
import com.palkesz.mr.x.core.util.extensions.validateAsName
import com.palkesz.mr.x.core.util.networking.DataLoader
import com.palkesz.mr.x.core.util.networking.RefreshTrigger
import com.palkesz.mr.x.core.util.networking.ViewState
import com.palkesz.mr.x.core.util.networking.map
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface CreateQuestionViewModel {
    val viewState: StateFlow<ViewState<CreateQuestionViewState>>

    fun onTextChanged(text: String)
    fun onFirstNameChanged(firstName: String)
    fun onLastNameChanged(lastName: String)
    fun onCreateClicked()
    fun onEventHandled()
}

class CreateQuestionViewModelImpl(
    private val gameId: String,
    private val createQuestionUseCase: CreateQuestionUseCase,
    gameRepository: GameRepository,
    konnectivity: Konnectivity,
) : ViewModel(), CreateQuestionViewModel {

    private val dataLoader = DataLoader<Unit>()

    private val refreshTrigger = RefreshTrigger()

    private val loadingResult = dataLoader.loadAndObserveDataAsState(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = ViewState.Success(Unit),
        fetchData = { createQuestion() },
    )

    private val questionText = MutableStateFlow("")

    private val isQuestionTextValid = MutableStateFlow(true)

    private val expectedFirstName = MutableStateFlow("")

    private val isExpectedFirstNameValid = MutableStateFlow(true)

    private val expectedLastName = MutableStateFlow("")

    private val isExpectedLastNameValid = MutableStateFlow(true)

    private val event = MutableStateFlow<CreateQuestionEvent?>(null)

    override val viewState = combine(
        loadingResult,
        gameRepository.games.map { games -> games.find { it.id == gameId } },
        questionText,
        isQuestionTextValid,
        expectedFirstName,
        isExpectedFirstNameValid,
        expectedLastName,
        isExpectedLastNameValid,
        event,
        konnectivity.isConnectedState,
    ) { result, game, text, isTextValid, firstName, isFirstNameValid, lastName, isLastNameValid, event, isConnected ->
        result.map {
            CreateQuestionViewState(
                text = text,
                firstName = firstName,
                lastName = lastName,
                isTextValid = isTextValid,
                isFirstNameValid = isFirstNameValid,
                isLastNameValid = isLastNameValid,
                isCreateButtonEnabled = isConnected && game?.status == GameStatus.ONGOING,
                event = event,
            )
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), ViewState.Success(
            CreateQuestionViewState(
                text = questionText.value,
                firstName = expectedFirstName.value,
                lastName = expectedLastName.value,
                isTextValid = isQuestionTextValid.value,
                isFirstNameValid = isExpectedFirstNameValid.value,
                isLastNameValid = isExpectedLastNameValid.value,
                isCreateButtonEnabled = konnectivity.isConnected,
            )
        )
    )

    override fun onTextChanged(text: String) {
        questionText.update { text }
        isQuestionTextValid.update { true }
    }

    override fun onFirstNameChanged(firstName: String) {
        expectedFirstName.update { firstName }
        isExpectedFirstNameValid.update { true }
    }

    override fun onLastNameChanged(lastName: String) {
        expectedLastName.update { lastName }
        isExpectedLastNameValid.update { true }
    }

    override fun onCreateClicked() {
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }

    override fun onEventHandled() {
        event.update { null }
    }

    private suspend fun createQuestion(): Result<Unit> {
        val isTextValid = questionText.value.isNotBlank()
        val isFirstNameValid =
            expectedFirstName.value.isNotBlank() && expectedFirstName.value.validateAsName()
        val isLastNameValid = expectedLastName.value.validateAsName()
        isQuestionTextValid.update { isTextValid }
        isExpectedFirstNameValid.update { isFirstNameValid }
        isExpectedLastNameValid.update { isLastNameValid }
        return if (isTextValid && isFirstNameValid && isLastNameValid) {
            createQuestionUseCase.run(
                text = questionText.value,
                firstName = expectedFirstName.value,
                lastName = expectedLastName.value,
                gameId = gameId,
            ).onSuccess {
                event.update { CreateQuestionEvent.NavigateUp }
            }
        } else {
            Result.success(Unit)
        }
    }

}
