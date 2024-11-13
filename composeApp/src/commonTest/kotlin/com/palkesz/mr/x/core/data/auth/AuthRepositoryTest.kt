package com.palkesz.mr.x.core.data.auth

import com.palkesz.mr.x.BaseTest
import dev.gitlive.firebase.auth.ActionCodeSettings
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthRepositoryTest : BaseTest() {

    @Test
    fun `Not logged in by default`() = runTest {
        val auth = object : FirebaseAuthentication.Stub {
            override val currentUser: FirebaseUser? = null
            override val authStateChanged: Flow<FirebaseUser?> = flowOf(value = null)
        }
        val repository = AuthRepositoryImpl(auth = auth)
        assertFalse { repository.isLoggedIn }
        assertFalse { repository.loggedIn.first() }
        assertEquals(expected = null, actual = repository.userId)
        assertEquals(expected = null, actual = repository.username)
    }

    @Test
    fun `Sending sign in link successfully`() = runTest {
        val auth = object : FirebaseAuthentication.Stub {
            override suspend fun sendSignInLinkToEmail(
                email: String,
                actionCodeSettings: ActionCodeSettings
            ) = Unit
        }
        val repository = AuthRepositoryImpl(auth = auth)
        assertEquals(
            expected = Result.success(Unit),
            actual = repository.sendSignInLinkToEmail(email = TEST_EMAIL),
        )
    }

    @Test
    fun `Sending sign in link with failure`() = runTest {
        val auth = object : FirebaseAuthentication.Stub {
            override suspend fun sendSignInLinkToEmail(
                email: String,
                actionCodeSettings: ActionCodeSettings
            ) = throw Exception()
        }
        val repository = AuthRepositoryImpl(auth = auth)
        assertTrue { repository.sendSignInLinkToEmail(email = TEST_EMAIL).isFailure }
    }

    @Test
    fun `Singing in with link with failure`() = runTest {
        val auth = object : FirebaseAuthentication.Stub {
            override suspend fun signInWithEmailLink(email: String, link: String) =
                throw Exception()
        }
        val repository = AuthRepositoryImpl(auth = auth)
        assertTrue {
            repository.signInWithEmailLink(email = TEST_EMAIL, link = TEST_LINK).isFailure
        }
    }

    companion object {
        private const val TEST_EMAIL = "test@gmail.com"
        private const val TEST_LINK = "mrx.firebaseapp.com"
    }
}
