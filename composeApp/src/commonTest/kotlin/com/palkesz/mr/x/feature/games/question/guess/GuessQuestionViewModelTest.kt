package com.palkesz.mr.x.feature.games.question.guess

import com.palkesz.mr.x.BaseTest
import com.palkesz.mr.x.KonnectivityStub
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.question.Answer
import com.palkesz.mr.x.core.model.question.Question
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.model.user.User
import com.palkesz.mr.x.core.usecase.question.GuessQuestionUseCase
import com.palkesz.mr.x.core.util.networking.ViewState
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GuessQuestionViewModelTest : BaseTest() {

    @Test
    fun `Default view state upon view model creation`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            question = TEST_QUESTION,
            guessQuestionResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = TEST_FIRST_NAME,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = "",
                    lastName = "",
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isAnswerButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Default view state upon view model creation when user is host`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING, hostId = TEST_USER_ID),
            question = TEST_QUESTION,
            guessQuestionResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = null,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = "",
                    lastName = "",
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isAnswerButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Answer question button is disable when game is finished`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.FINISHED),
            question = TEST_QUESTION,
            guessQuestionResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = TEST_FIRST_NAME,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = "",
                    lastName = "",
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isAnswerButtonEnabled = false,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Answer question button disabled when offline`() = runTest {
        val isConnectedState = flowOf(value = false).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = false
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            question = TEST_QUESTION,
            guessQuestionResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = TEST_FIRST_NAME,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = "",
                    lastName = "",
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isAnswerButtonEnabled = false,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Updating first name`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            question = TEST_QUESTION,
            guessQuestionResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onFirstNameChanged(firstName = TEST_FIRST_NAME)
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = TEST_FIRST_NAME,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = TEST_FIRST_NAME,
                    lastName = "",
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isAnswerButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Updating last name`() = runTest {
        val isConnectedState = flowOf(true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            question = TEST_QUESTION,
            guessQuestionResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onLastNameChanged(lastName = TEST_LAST_NAME)
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = TEST_FIRST_NAME,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = "",
                    lastName = TEST_LAST_NAME,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isAnswerButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Answer question validation fails because name and text is blank`() = runTest {
        val isConnectedState = flowOf(true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            question = TEST_QUESTION,
            guessQuestionResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onAnswerClicked()
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = TEST_FIRST_NAME,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = "",
                    lastName = "",
                    isFirstNameValid = false,
                    isLastNameValid = true,
                    isAnswerButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Answer question validation fails because name is not valid`() = runTest {
        val isConnectedState = flowOf(true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            question = TEST_QUESTION,
            guessQuestionResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onLastNameChanged(lastName = "1")
        viewModel.onAnswerClicked()
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = TEST_FIRST_NAME,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = "",
                    lastName = "1",
                    isFirstNameValid = false,
                    isLastNameValid = false,
                    isAnswerButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
        viewModel.apply {
            onFirstNameChanged(firstName = TEST_FIRST_NAME)
            onLastNameChanged(lastName = TEST_LAST_NAME)
        }
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = TEST_FIRST_NAME,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = TEST_FIRST_NAME,
                    lastName = TEST_LAST_NAME,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isAnswerButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Answer question validation fails because name is not the right initial`() = runTest {
        val isConnectedState = flowOf(true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            question = TEST_QUESTION,
            guessQuestionResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.apply {
            onFirstNameChanged(firstName = TEST_FIRST_NAME)
            onLastNameChanged(lastName = TEST_WRONG_LAST_NAME)
            onAnswerClicked()
        }
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = TEST_FIRST_NAME,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = TEST_FIRST_NAME,
                    lastName = TEST_WRONG_LAST_NAME,
                    isFirstNameValid = true,
                    isLastNameValid = false,
                    isAnswerButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Answer question successfully`() = runTest {
        val isConnectedState = flowOf(true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING, lastName = TEST_LAST_NAME),
            question = TEST_QUESTION,
            guessQuestionResult = Result.success(Unit),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.apply {
            onFirstNameChanged(firstName = TEST_FIRST_NAME)
            onLastNameChanged(lastName = TEST_LAST_NAME)
            onAnswerClicked()
        }
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = TEST_FIRST_NAME,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = TEST_FIRST_NAME,
                    lastName = TEST_LAST_NAME,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isAnswerButtonEnabled = true,
                    event = GuessQuestionEvent.NavigateUp,
                )
            ),
            actual = viewModel.viewState.value
        )
        viewModel.onEventHandled()
        assertEquals(
            expected = ViewState.Success(
                GuessQuestionViewState(
                    questionText = TEST_QUESTION_TEXT,
                    hostAnswer = TEST_FIRST_NAME,
                    number = 1,
                    owner = TEST_PLAYER_NAME,
                    firstName = TEST_FIRST_NAME,
                    lastName = TEST_LAST_NAME,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isAnswerButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Answer question failure`() = runTest {
        val isConnectedState = flowOf(true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING, lastName = TEST_LAST_NAME),
            question = TEST_QUESTION,
            guessQuestionResult = Result.failure(exception = Throwable()),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.apply {
            onFirstNameChanged(firstName = TEST_FIRST_NAME)
            onLastNameChanged(lastName = TEST_LAST_NAME)
            onAnswerClicked()
        }
        assertTrue { viewModel.viewState.value is ViewState.Failure }
    }

    private fun getTestGame(status: GameStatus, hostId: String? = null, lastName: String? = null) =
        Game(
            id = TEST_GAME_ID,
            firstName = TEST_FIRST_NAME,
            lastName = lastName,
            hostId = hostId.orEmpty(),
            status = status,
            lastModifiedTimestamp = Timestamp.now()
        )

    private fun getViewModel(
        game: Game,
        question: Question,
        guessQuestionResult: Result<Unit>,
        konnectivity: KonnectivityStub,
    ): GuessQuestionViewModel {
        val guessQuestionUseCase = GuessQuestionUseCase { _, _, _ -> guessQuestionResult }
        val authRepository = object : AuthRepository.Stub {
            override val userId = TEST_USER_ID
        }
        val usersRepository = object : UserRepository.Stub {
            override val users =
                MutableStateFlow(listOf(User(id = TEST_PLAYER_ID, name = TEST_PLAYER_NAME)))
        }
        val gameRepository = object : GameRepository.Stub {
            override val games = MutableStateFlow(listOf(game))
        }
        val questionRepository = object : QuestionRepository.Stub {
            override val questions = MutableStateFlow(listOf(question))
        }
        return GuessQuestionViewModelImpl(
            gameId = TEST_GAME_ID,
            questionId = TEST_QUESTION_ID,
            guessQuestionUseCase = guessQuestionUseCase,
            authRepository = authRepository,
            userRepository = usersRepository,
            gameRepository = gameRepository,
            questionRepository = questionRepository,
            konnectivity = konnectivity,
        )
    }

    companion object {
        private const val TEST_FIRST_NAME = "John"
        private const val TEST_LAST_NAME = "Doe"
        private const val TEST_WRONG_LAST_NAME = "Smith"
        private const val TEST_GAME_ID = "TEST_GAME_ID"
        private const val TEST_QUESTION_ID = "TEST_QUESTION_ID"
        private const val TEST_USER_ID = "TEST_USER_ID"
        private const val TEST_PLAYER_ID = "TEST_PLAYER_ID"
        private const val TEST_PLAYER_NAME = "TEST_PLAYER_NAME"
        private const val TEST_QUESTION_TEXT = "TEST_QUESTION_TEXT"
        private val TEST_QUESTION = Question(
            id = TEST_QUESTION_ID,
            userId = TEST_PLAYER_ID,
            gameId = TEST_GAME_ID,
            number = 1,
            expectedFirstName = TEST_FIRST_NAME,
            expectedLastName = TEST_LAST_NAME,
            hostAnswer = Answer(firstName = TEST_FIRST_NAME, lastName = null, userId = ""),
            playerAnswer = null,
            status = QuestionStatus.WAITING_FOR_PLAYERS,
            text = TEST_QUESTION_TEXT,
            lastModifiedTimestamp = Timestamp.now(),
        )
    }
}
