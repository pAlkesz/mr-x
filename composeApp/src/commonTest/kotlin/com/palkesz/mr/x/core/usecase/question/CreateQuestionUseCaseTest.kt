package com.palkesz.mr.x.core.usecase.question

import com.palkesz.mr.x.BaseTest
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.NO_USER_ID_FOUND_MESSAGE
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.question.Question
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateQuestionUseCaseTest : BaseTest() {

    @Test
    fun `Error when no username found`() = runTest {
        val useCase = getUseCase(userId = null)
        assertEquals(
            expected = NO_USER_ID_FOUND_MESSAGE,
            actual = useCase.run(text = "", lastName = "", firstName = "", gameId = "")
                .exceptionOrNull()?.message
        )
    }

    @Test
    fun `Create question successfully`() = runTest {
        val gameRepository = object : GameRepository.Stub {
            override val games = MutableStateFlow(
                listOf(
                    Game(
                        id = TEST_GAME_ID,
                        firstName = TEST_FIRST_NAME,
                        lastName = TEST_LAST_NAME,
                        hostId = "",
                        status = GameStatus.ONGOING,
                        lastModifiedTimestamp = Timestamp.now(),
                    )
                )
            )

            override suspend fun updateStatus(id: String, status: GameStatus) = Result.success(Unit)
        }
        val useCase = getUseCase(gameRepository = gameRepository)
        assertEquals(
            expected = Result.success(Unit),
            actual = useCase.run(text = "", lastName = "", firstName = "", gameId = TEST_GAME_ID)
        )
    }

    @Test
    fun `Create question failure`() = runTest {
        val gameRepository = object : GameRepository.Stub {
            override val games = MutableStateFlow(
                listOf(
                    Game(
                        id = TEST_GAME_ID,
                        firstName = TEST_FIRST_NAME,
                        lastName = TEST_LAST_NAME,
                        hostId = "",
                        status = GameStatus.ONGOING,
                        lastModifiedTimestamp = Timestamp.now(),
                    )
                )
            )

            override suspend fun updateStatus(id: String, status: GameStatus) = Result.success(Unit)
        }
        val useCase = getUseCase(
            gameRepository = gameRepository,
            createQuestionResult = Result.failure(exception = Throwable())
        )
        assertTrue {
            useCase.run(
                text = "",
                lastName = "",
                firstName = "",
                gameId = TEST_GAME_ID,
            ).isFailure
        }
    }

    @Test
    fun `Create winning question successfully`() = runTest {
        var gameStatus = GameStatus.ONGOING
        val gameRepository = object : GameRepository.Stub {
            override val games = MutableStateFlow(
                listOf(
                    Game(
                        id = TEST_GAME_ID,
                        firstName = TEST_FIRST_NAME,
                        lastName = TEST_LAST_NAME,
                        hostId = "",
                        status = GameStatus.ONGOING,
                        lastModifiedTimestamp = Timestamp.now(),
                    )
                )
            )

            override suspend fun updateStatus(id: String, status: GameStatus): Result<Unit> {
                gameStatus = status
                return Result.success(Unit)
            }
        }
        val useCase = getUseCase(gameRepository = gameRepository)
        useCase.run(
            text = "",
            lastName = TEST_NORMALIZED_LAST_NAME,
            firstName = TEST_NORMALIZED_FIRST_NAME,
            gameId = TEST_GAME_ID
        )
        assertEquals(expected = GameStatus.FINISHED, actual = gameStatus)
    }

    @Test
    fun `Create winning question failure`() = runTest {
        val gameRepository = object : GameRepository.Stub {
            override val games = MutableStateFlow(
                listOf(
                    Game(
                        id = TEST_GAME_ID,
                        firstName = TEST_FIRST_NAME,
                        lastName = TEST_LAST_NAME,
                        hostId = "",
                        status = GameStatus.ONGOING,
                        lastModifiedTimestamp = Timestamp.now(),
                    )
                )
            )

            override suspend fun updateStatus(id: String, status: GameStatus): Result<Unit> =
                Result.failure(Throwable())
        }
        val useCase = getUseCase(gameRepository = gameRepository)
        assertTrue {
            useCase.run(
                text = "",
                lastName = TEST_NORMALIZED_LAST_NAME,
                firstName = TEST_NORMALIZED_FIRST_NAME,
                gameId = TEST_GAME_ID,
            ).isFailure
        }
    }

    private fun getUseCase(
        userId: String? = TEST_USER_ID,
        createQuestionResult: Result<Unit> = Result.success(Unit),
        gameRepository: GameRepository = object : GameRepository.Stub {
            override val games = MutableStateFlow<List<Game>>(emptyList())

            override suspend fun updateStatus(id: String, status: GameStatus) =
                Result.success(Unit)
        }
    ): CreateQuestionUseCase {
        val questionRepository = object : QuestionRepository.Stub {
            override val questions = MutableStateFlow<List<Question>>(value = emptyList())

            override suspend fun createQuestion(question: Question) = createQuestionResult
        }
        val authRepository = object : AuthRepository.Stub {
            override val userId = userId
        }
        return CreateQuestionUseCaseImpl(
            questionRepository = questionRepository,
            authRepository = authRepository,
            gameRepository = gameRepository
        )
    }

    companion object {
        private const val TEST_USER_ID = "TEST_USER_ID"
        private const val TEST_GAME_ID = "TEST_GAME_ID"
        private const val TEST_FIRST_NAME = "Kovács"
        private const val TEST_NORMALIZED_FIRST_NAME = "Kovacs"
        private const val TEST_LAST_NAME = "János"
        private const val TEST_NORMALIZED_LAST_NAME = "Janos"
    }
}
