package com.palkesz.mr.x.feature.games.question.guess

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.usecase.question.GuessQuestionUseCase
import com.palkesz.mr.x.core.util.extensions.combine
import com.palkesz.mr.x.core.util.extensions.getInitial
import com.palkesz.mr.x.core.util.extensions.getName
import com.palkesz.mr.x.core.util.extensions.validateAsName
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

interface GuessQuestionViewModel {
    val viewState: StateFlow<ViewState<GuessQuestionViewState>>

    fun onFirstNameChanged(firstName: String)
    fun onLastNameChanged(lastName: String)
    fun onAnswerClicked()
    fun onEventHandled()
}

@Stable
class GuessQuestionViewModelImpl(
    private val gameId: String,
    private val questionId: String,
    private val guessQuestionUseCase: GuessQuestionUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    questionRepository: QuestionRepository,
    konnectivity: Konnectivity,
) : ViewModel(), GuessQuestionViewModel {

    private val dataLoader = DataLoader<Unit>()

    private val refreshTrigger = RefreshTrigger()

    private val loadingResult = dataLoader.loadAndObserveDataAsState(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = ViewState.Success(Unit),
        fetchData = { guessQuestion() },
    )

    private val firstName = MutableStateFlow("")

    private val isFirstNameValid = MutableStateFlow(true)

    private val lastName = MutableStateFlow("")

    private val isLastNameValid = MutableStateFlow(true)

    private val event = MutableStateFlow<GuessQuestionEvent?>(null)

    override val viewState = combine(
        loadingResult,
        questionRepository.questions.map { questions ->
            questions.first { it.id == questionId }
        }.distinctUntilChanged(),
        gameRepository.games.map { games ->
            games.find { it.id == gameId }
        }.distinctUntilChanged(),
        firstName,
        lastName,
        isFirstNameValid,
        isLastNameValid,
        event,
        konnectivity.isConnectedState,
    ) { result, question, game, firstName, lastName, isFirstNameValid, isLastNameValid, event, isConnected ->
        result.map {
            GuessQuestionViewState(
                questionText = question.text,
                hostAnswer = question.hostAnswer?.getName()?.takeIf {
                    game?.hostId != authRepository.userId
                },
                number = question.number,
                owner = userRepository.users.value.first { it.id == question.userId }.name,
                firstName = firstName,
                lastName = lastName,
                isFirstNameValid = isFirstNameValid,
                isLastNameValid = isLastNameValid,
                isAnswerButtonEnabled = isConnected && game?.status == GameStatus.ONGOING,
                event = event,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ViewState.Loading)

    override fun onFirstNameChanged(firstName: String) {
        this.firstName.update { firstName }
        isFirstNameValid.update { true }
    }

    override fun onLastNameChanged(lastName: String) {
        this.lastName.update { lastName }
        isLastNameValid.update { true }
    }

    override fun onAnswerClicked() {
        viewModelScope.launch {
            refreshTrigger.refresh()
        }
    }

    override fun onEventHandled() {
        event.update { null }
    }

    private suspend fun guessQuestion(): Result<Unit> {
        val gameInitial = gameRepository.games.value.find { it.id == gameId }?.getInitial()
        val (isFirstNameValid, isLastNameValid) =
            (firstName.value to lastName.value)
                .validateAsName(gameInitial = gameInitial)
        this.isFirstNameValid.update { isFirstNameValid }
        this.isLastNameValid.update { isLastNameValid }
        return if (isFirstNameValid && isLastNameValid) {
            guessQuestionUseCase.run(
                questionId = questionId,
                firstName = firstName.value,
                lastName = lastName.value,
            ).onSuccess {
                event.update { GuessQuestionEvent.NavigateUp }
            }
        } else {
            Result.success(Unit)
        }
    }
}
