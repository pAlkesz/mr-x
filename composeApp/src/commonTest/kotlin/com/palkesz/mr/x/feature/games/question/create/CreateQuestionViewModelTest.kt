package com.palkesz.mr.x.feature.games.question.create

import com.palkesz.mr.x.BaseTest
import com.palkesz.mr.x.KonnectivityStub
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.question.Question
import com.palkesz.mr.x.core.model.question.QuestionStatus
import com.palkesz.mr.x.core.usecase.question.CreateQuestionUseCase
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

class CreateQuestionViewModelTest : BaseTest() {

    @Test
    fun `Default view state upon view model creation`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            createQuestionResult = Result.success(value = TEST_QUESTION),
            konnectivity = konnectivity,
        )
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = "",
                    firstName = "",
                    lastName = "",
                    isTextValid = true,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isCreateButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Create question button is disable when game is finished`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.FINISHED),
            createQuestionResult = Result.success(value = TEST_QUESTION),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = "",
                    firstName = "",
                    lastName = "",
                    isTextValid = true,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isCreateButtonEnabled = false,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Create question button disabled when offline`() = runTest {
        val isConnectedState = flowOf(value = false).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = false
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            createQuestionResult = Result.success(value = TEST_QUESTION),
            konnectivity = konnectivity,
        )
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = "",
                    firstName = "",
                    lastName = "",
                    isTextValid = true,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isCreateButtonEnabled = false,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Updating question text`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            createQuestionResult = Result.success(value = TEST_QUESTION),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onTextChanged(text = TEST_QUESTION_TEXT)
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = TEST_QUESTION_TEXT,
                    firstName = "",
                    lastName = "",
                    isTextValid = true,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isCreateButtonEnabled = true,
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
            createQuestionResult = Result.success(value = TEST_QUESTION),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onFirstNameChanged(firstName = TEST_FIRST_NAME)
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = "",
                    firstName = TEST_FIRST_NAME,
                    lastName = "",
                    isTextValid = true,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isCreateButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Updating last name`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            createQuestionResult = Result.success(value = TEST_QUESTION),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onLastNameChanged(lastName = TEST_LAST_NAME)
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = "",
                    firstName = "",
                    lastName = TEST_LAST_NAME,
                    isTextValid = true,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isCreateButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Create question validation fails because name and text is blank`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            createQuestionResult = Result.success(value = TEST_QUESTION),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onCreateClicked()
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = "",
                    firstName = "",
                    lastName = "",
                    isTextValid = false,
                    isFirstNameValid = false,
                    isLastNameValid = true,
                    isCreateButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Create question validation fails because name is not valid`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            createQuestionResult = Result.success(value = TEST_QUESTION),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.onLastNameChanged(lastName = "1")
        viewModel.onCreateClicked()
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = "",
                    firstName = "",
                    lastName = "1",
                    isTextValid = false,
                    isFirstNameValid = false,
                    isLastNameValid = false,
                    isCreateButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
        viewModel.apply {
            onTextChanged(text = TEST_QUESTION_TEXT)
            onFirstNameChanged(firstName = TEST_FIRST_NAME)
            onLastNameChanged(lastName = TEST_LAST_NAME)
        }
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = TEST_QUESTION_TEXT,
                    firstName = TEST_FIRST_NAME,
                    lastName = TEST_LAST_NAME,
                    isTextValid = true,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isCreateButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Create question validation fails because name is not the right initial`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING),
            createQuestionResult = Result.success(value = TEST_QUESTION),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.apply {
            onTextChanged(text = TEST_QUESTION_TEXT)
            onFirstNameChanged(firstName = TEST_FIRST_NAME)
            onLastNameChanged(lastName = TEST_WRONG_LAST_NAME)
            onCreateClicked()
        }
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = TEST_QUESTION_TEXT,
                    firstName = TEST_FIRST_NAME,
                    lastName = TEST_WRONG_LAST_NAME,
                    isTextValid = true,
                    isFirstNameValid = true,
                    isLastNameValid = false,
                    isCreateButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Create question successfully`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING, lastName = TEST_LAST_NAME),
            createQuestionResult = Result.success(value = TEST_QUESTION),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.apply {
            onTextChanged(text = TEST_QUESTION_TEXT)
            onFirstNameChanged(firstName = TEST_FIRST_NAME)
            onLastNameChanged(lastName = TEST_LAST_NAME)
            onCreateClicked()
        }
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = TEST_QUESTION_TEXT,
                    firstName = TEST_FIRST_NAME,
                    lastName = TEST_LAST_NAME,
                    isTextValid = true,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isCreateButtonEnabled = true,
                    event = CreateQuestionEvent.NavigateUp(
                        gameId = TEST_GAME_ID,
                        questionId = TEST_QUESTION_ID,
                    ),
                )
            ),
            actual = viewModel.viewState.value
        )
        viewModel.onEventHandled()
        assertEquals(
            expected = ViewState.Success(
                CreateQuestionViewState(
                    text = TEST_QUESTION_TEXT,
                    firstName = TEST_FIRST_NAME,
                    lastName = TEST_LAST_NAME,
                    isTextValid = true,
                    isFirstNameValid = true,
                    isLastNameValid = true,
                    isCreateButtonEnabled = true,
                    event = null,
                )
            ),
            actual = viewModel.viewState.value
        )
    }

    @Test
    fun `Create question failure`() = runTest {
        val isConnectedState = flowOf(value = true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(
            game = getTestGame(status = GameStatus.ONGOING, lastName = TEST_LAST_NAME),
            createQuestionResult = Result.failure(exception = Throwable()),
            konnectivity = konnectivity,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect()
        }
        viewModel.apply {
            onTextChanged(text = TEST_QUESTION_TEXT)
            onFirstNameChanged(firstName = TEST_FIRST_NAME)
            onLastNameChanged(lastName = TEST_LAST_NAME)
            onCreateClicked()
        }
        assertTrue { viewModel.viewState.value is ViewState.Failure }
    }

    private fun getTestGame(status: GameStatus, lastName: String? = null) = Game(
        id = TEST_GAME_ID,
        firstName = TEST_FIRST_NAME,
        lastName = lastName,
        hostId = "",
        status = status,
        lastModifiedTimestamp = Timestamp.now()
    )

    private fun getViewModel(
        game: Game,
        createQuestionResult: Result<Question>,
        konnectivity: KonnectivityStub,
    ): CreateQuestionViewModel {
        val createQuestionUseCase = CreateQuestionUseCase { _, _, _, _ -> createQuestionResult }
        val gameRepository = object : GameRepository.Stub {
            override val games = MutableStateFlow(listOf(game))
        }
        return CreateQuestionViewModelImpl(
            gameId = TEST_GAME_ID,
            createQuestionUseCase = createQuestionUseCase,
            gameRepository = gameRepository,
            konnectivity = konnectivity,
        )
    }

    companion object {
        private const val TEST_QUESTION_ID = "TEST_QUESTION_ID"
        private val TEST_QUESTION = Question(
            id = TEST_QUESTION_ID,
            userId = "",
            gameId = "",
            number = 1,
            expectedFirstName = "",
            expectedLastName = null,
            hostAnswer = null,
            playerAnswer = null,
            status = QuestionStatus.WAITING_FOR_HOST,
            text = "",
            lastModifiedTimestamp = Timestamp.now(),
        )
        private const val TEST_FIRST_NAME = "John"
        private const val TEST_LAST_NAME = "Doe"
        private const val TEST_WRONG_LAST_NAME = "Smith"
        private const val TEST_GAME_ID = "TEST_GAME_ID"
        private const val TEST_QUESTION_TEXT = "TEST_QUESTION_TEXT"
    }

}
