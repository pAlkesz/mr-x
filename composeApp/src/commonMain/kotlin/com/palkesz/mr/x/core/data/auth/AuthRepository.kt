package com.palkesz.mr.x.core.data.auth

import com.palkesz.mr.x.core.util.extensions.getSignInLink
import dev.gitlive.firebase.auth.ActionCodeSettings
import dev.gitlive.firebase.auth.AndroidPackageName
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.theolm.rinku.DeepLink
import io.github.aakira.napier.Napier

interface AuthRepository {
    val userId: String?
    val username: String?
    val isLoggedIn: Boolean

    suspend fun sendSignInLinkToEmail(email: String): Result<Unit>
    suspend fun signInWithEmailLink(email: String, link: String): Result<AuthResult>
    suspend fun updateUsername(name: String): Result<Unit>
    fun isSignInLink(link: DeepLink): Boolean
}

class AuthRepositoryImpl(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val userId: String?
        get() = auth.currentUser?.uid

    override val username: String?
        get() = auth.currentUser?.displayName

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override suspend fun sendSignInLinkToEmail(email: String): Result<Unit> {
        val actionCodeSettings = ActionCodeSettings(
            url = SIGN_IN_LINK,
            canHandleCodeInApp = true,
            iOSBundleId = APP_ID,
            androidPackageName = AndroidPackageName(APP_ID, installIfNotAvailable = true),
        )
        return try {
            auth.sendSignInLinkToEmail(email = email, actionCodeSettings = actionCodeSettings)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception = exception)
        }
    }

    override suspend fun signInWithEmailLink(email: String, link: String): Result<AuthResult> =
        try {
            Result.success(auth.signInWithEmailLink(email, link = link))
        } catch (exception: Exception) {
            Result.failure(exception = exception)
        }

    override suspend fun updateUsername(name: String) =
        try {
            auth.currentUser?.updateProfile(displayName = name)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception = exception)
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
        private const val APP_ID = "com.palkesz.mr.x"
        private const val SIGN_IN_LINK = "https://mrxapp.page.link/signIn"
    }

}
