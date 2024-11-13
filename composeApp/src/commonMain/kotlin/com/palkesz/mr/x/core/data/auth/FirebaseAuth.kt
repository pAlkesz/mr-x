package com.palkesz.mr.x.core.data.auth

import dev.gitlive.firebase.auth.ActionCodeSettings
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthentication {
    val currentUser: FirebaseUser?
    val authStateChanged: Flow<FirebaseUser?>

    fun isSignInWithEmailLink(link: String): Boolean
    suspend fun sendSignInLinkToEmail(email: String, actionCodeSettings: ActionCodeSettings)
    suspend fun signInWithEmailLink(email: String, link: String): AuthResult

    interface Stub : FirebaseAuthentication {
        override val currentUser: FirebaseUser?
            get() = throw NotImplementedError()
        override val authStateChanged: Flow<FirebaseUser?>
            get() = throw NotImplementedError()

        override fun isSignInWithEmailLink(link: String): Boolean = throw NotImplementedError()
        override suspend fun sendSignInLinkToEmail(
            email: String,
            actionCodeSettings: ActionCodeSettings
        ): Unit = throw NotImplementedError()

        override suspend fun signInWithEmailLink(email: String, link: String): AuthResult =
            throw NotImplementedError()
    }
}

class FirebaseAuthenticationImpl(
    private val auth: FirebaseAuth,
) : FirebaseAuthentication {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val authStateChanged = auth.authStateChanged

    override fun isSignInWithEmailLink(link: String) = auth.isSignInWithEmailLink(link = link)

    override suspend fun sendSignInLinkToEmail(
        email: String,
        actionCodeSettings: ActionCodeSettings
    ) = auth.sendSignInLinkToEmail(email = email, actionCodeSettings = actionCodeSettings)

    override suspend fun signInWithEmailLink(email: String, link: String) =
        auth.signInWithEmailLink(email = email, link = link)

}
