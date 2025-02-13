package com.palkesz.mr.x.core.usecase.auth

import com.palkesz.mr.x.core.util.BUSINESS_TAG
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import io.github.aakira.napier.Napier

fun interface SignInWithPasswordUseCase {
    suspend fun run(email: String, password: String): Result<Unit>
}

class SignInWithPasswordUseCaseImpl(
    private val firebaseAuth: FirebaseAuth,
) : SignInWithPasswordUseCase {

    override suspend fun run(email: String, password: String) = runCatching {
        val result = firebaseAuth.signInWithCredential(
            authCredential = EmailAuthProvider.credential(
                email = email,
                password = password,
            )
        )
        Napier.d(tag = BUSINESS_TAG) { "Password sign in result: $result" }
    }
}
