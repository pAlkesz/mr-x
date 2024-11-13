package com.palkesz.mr.x.core.usecase.auth

import com.palkesz.mr.x.BaseTest
import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.core.model.user.User
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsUserNameUploadedUseCaseTest : BaseTest() {

    @Test
    fun `Username not uploaded when logged out`() = runTest {
        val authRepository = object : AuthRepository.Stub {
            override val userId: String? = null
            override val username: String? = null
        }
        val userRepository = object : UserRepository.Stub {
            override suspend fun fetchUser(id: String): Result<User> {
                return Result.failure(exception = Exception())
            }
        }
        val useCase = IsUsernameUploadedUseCaseImpl(
            authRepository = authRepository,
            userRepository = userRepository,
        )
        assertFalse { useCase.run() }
    }

    @Test
    fun `Username uploaded when logged in`() = runTest {
        val authRepository = object : AuthRepository.Stub {
            override val userId: String = TEST_USER_ID
            override val username: String = TEST_USER_NAME
        }
        val userRepository = object : UserRepository.Stub {
            override suspend fun fetchUser(id: String): Result<User> {
                return Result.success(User(id = TEST_USER_ID, name = TEST_USER_NAME))
            }
        }
        val useCase = IsUsernameUploadedUseCaseImpl(
            authRepository = authRepository,
            userRepository = userRepository,
        )
        assertTrue { useCase.run() }
    }

    companion object {
        private const val TEST_USER_ID = "USER_ID"
        private const val TEST_USER_NAME = "USER_NAME"
    }
}
