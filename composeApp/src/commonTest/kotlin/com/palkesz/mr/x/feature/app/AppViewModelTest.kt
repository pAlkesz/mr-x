package com.palkesz.mr.x.feature.app

import com.palkesz.mr.x.BaseTest
import com.palkesz.mr.x.KonnectivityStub
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.crashlytics.Crashlytics
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.usecase.game.JoinGameUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppViewModelTest : BaseTest() {

    @Test
    fun `Default view state upon view model creation`() = runTest {
        val isConnectedState = flowOf(true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        val viewModel = getViewModel(isLoggedIn = false, konnectivity = konnectivity)
        assertEquals(
            expected = AppViewState(isLoggedIn = false, isOfflineBarVisible = false, event = null),
            actual = viewModel.viewState.value,
        )
    }

    @Test
    fun `Observe data when logged in`() = runTest {
        val isConnectedState = flowOf(true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        var isObservingGames = false
        var isObservingQuestions = false
        var isObservingBarkochbaQuestions = false
        var isObservingUsers = false
        getViewModel(
            isLoggedIn = true,
            konnectivity = konnectivity,
            gameRepository = object : GameRepository.Stub {
                override suspend fun observeGames() {
                    isObservingGames = true
                }
            },
            questionRepository = object : QuestionRepository.Stub {
                override suspend fun observeQuestions() {
                    isObservingQuestions = true
                }
            },
            barkochbaQuestionRepository = object : BarkochbaQuestionRepository.Stub {
                override suspend fun observeQuestions() {
                    isObservingBarkochbaQuestions = true
                }
            },
            userRepository = object : UserRepository.Stub {
                override suspend fun observeUsers() {
                    isObservingUsers = true
                }
            },
        )
        assertTrue { isObservingGames }
        assertTrue { isObservingQuestions }
        assertTrue { isObservingBarkochbaQuestions }
        assertTrue { isObservingUsers }
    }

    @Test
    fun `Do not observe data when not logged in`() = runTest {
        val isConnectedState = flowOf(true).stateIn(backgroundScope)
        val konnectivity = object : KonnectivityStub {
            override val isConnected = true
            override val isConnectedState = isConnectedState
        }
        var isObservingGames = false
        var isObservingQuestions = false
        var isObservingBarkochbaQuestions = false
        var isObservingUsers = false
        getViewModel(
            isLoggedIn = false,
            konnectivity = konnectivity,
            gameRepository = object : GameRepository.Stub {
                override suspend fun observeGames() {
                    isObservingGames = true
                }
            },
            questionRepository = object : QuestionRepository.Stub {
                override suspend fun observeQuestions() {
                    isObservingQuestions = true
                }
            },
            barkochbaQuestionRepository = object : BarkochbaQuestionRepository.Stub {
                override suspend fun observeQuestions() {
                    isObservingBarkochbaQuestions = true
                }
            },
            userRepository = object : UserRepository.Stub {
                override suspend fun observeUsers() {
                    isObservingUsers = true
                }
            },
        )
        assertFalse { isObservingGames }
        assertFalse { isObservingQuestions }
        assertFalse { isObservingBarkochbaQuestions }
        assertFalse { isObservingUsers }
    }

    private fun getViewModel(
        isLoggedIn: Boolean,
        konnectivity: KonnectivityStub,
        gameRepository: GameRepository = object : GameRepository.Stub {
            override suspend fun observeGames() = Unit
        },
        questionRepository: QuestionRepository = object : QuestionRepository.Stub {
            override suspend fun observeQuestions() = Unit
        },
        barkochbaQuestionRepository: BarkochbaQuestionRepository = object :
            BarkochbaQuestionRepository.Stub {
            override suspend fun observeQuestions() = Unit
        },
        userRepository: UserRepository = object : UserRepository.Stub {
            override suspend fun observeUsers() = Unit
        },
        crashlytics: Crashlytics = object : Crashlytics.Stub {
            override fun setCrashlyticsCollectionEnabled(enabled: Boolean) = Unit
            override fun setUserId(userId: String) = Unit
        }
    ): AppViewModel {
        val authRepository = object : AuthRepository.Stub {
            override val userId = TEST_USER_ID
            override val isLoggedIn = isLoggedIn
            override val loggedIn = flowOf(isLoggedIn)
        }
        val joinGameUseCase = JoinGameUseCase { Result.success(Unit) }
        return AppViewModelImpl(
            authRepository = authRepository,
            gameRepository = gameRepository,
            questionRepository = questionRepository,
            barkochbaQuestionRepository = barkochbaQuestionRepository,
            userRepository = userRepository,
            joinGameUseCase = joinGameUseCase,
            konnectivity = konnectivity,
            crashlytics = crashlytics,
        )
    }

    companion object {
        private const val TEST_USER_ID = "TEST_USER_ID"
    }
}
