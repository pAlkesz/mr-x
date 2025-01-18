package com.palkesz.mr.x.core.data.auth

import com.palkesz.mr.x.core.util.extensions.getSignInLink
import dev.gitlive.firebase.auth.ActionCodeSettings
import dev.gitlive.firebase.auth.AndroidPackageName
import dev.theolm.rinku.DeepLink
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AuthRepository {
    val userId: String?
    val username: String?
    val isLoggedIn: Boolean
    val loggedIn: Flow<Boolean>

    suspend fun sendSignInLinkToEmail(email: String): Result<Unit>
    suspend fun signInWithEmailLink(email: String, link: String): Result<Unit>
    suspend fun updateUsername(name: String): Result<Unit>
    fun isSignInLink(link: DeepLink): Boolean

    interface Stub : AuthRepository {
        override val userId: String?
            get() = throw NotImplementedError()
        override val username: String?
            get() = throw NotImplementedError()
        override val isLoggedIn: Boolean
            get() = throw NotImplementedError()
        override val loggedIn: Flow<Boolean>
            get() = throw NotImplementedError()

        override suspend fun updateUsername(name: String): Result<Unit> =
            throw NotImplementedError()

        override fun isSignInLink(link: DeepLink): Boolean = throw NotImplementedError()
        override suspend fun sendSignInLinkToEmail(email: String): Result<Unit> =
            throw NotImplementedError()

        override suspend fun signInWithEmailLink(email: String, link: String): Result<Unit> =
            throw NotImplementedError()
    }
}

class AuthRepositoryImpl(
    private val auth: FirebaseAuthentication
) : AuthRepository {

    override val userId: String?
        get() = auth.currentUser?.uid

    override val username: String?
        get() = auth.currentUser?.displayName

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override val loggedIn: Flow<Boolean>
        get() = auth.authStateChanged.map { it != null }

    override suspend fun sendSignInLinkToEmail(email: String) = runCatching {
        val actionCodeSettings = ActionCodeSettings(
            url = SIGN_IN_LINK,
            canHandleCodeInApp = true,
            iOSBundleId = APP_ID,
            androidPackageName = AndroidPackageName(APP_ID, installIfNotAvailable = true),
        )
        auth.sendSignInLinkToEmail(email = email, actionCodeSettings = actionCodeSettings)
    }

    override suspend fun signInWithEmailLink(email: String, link: String) = runCatching {
        auth.signInWithEmailLink(email = email, link = link)
        Unit
    }

    override suspend fun updateUsername(name: String) = runCatching {
        auth.currentUser?.updateProfile(displayName = name) ?: Unit
    }

    override fun isSignInLink(link: DeepLink) =
        link.getSignInLink()?.let { signInLink ->
            auth.isSignInWithEmailLink(link = signInLink).also {
                Napier.d(tag = AUTH_TAG) { "$signInLink is sign in link: $it" }
            }
        } ?: false

    companion object {
        const val AUTH_TAG = "AUTH_TAG"
        const val LINK_PARAMETER_KEY = "link"
        const val NO_USER_ID_FOUND_MESSAGE = "NO_USER_ID_FOUND"
        private const val APP_ID = "com.palkesz.mr.x"
        private const val SIGN_IN_LINK = "https://mrxapp.page.link/signIn"
    }

}
