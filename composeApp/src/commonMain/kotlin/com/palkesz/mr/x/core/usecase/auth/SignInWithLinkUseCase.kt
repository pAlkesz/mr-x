package com.palkesz.mr.x.core.usecase.auth

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.datastore.MrxDataStore
import com.palkesz.mr.x.core.util.extensions.getSignInLink
import dev.gitlive.firebase.auth.AuthResult
import dev.theolm.rinku.DeepLink

class SignInWithLinkUseCase(
    private val authRepository: AuthRepository,
    private val mrxDataStore: MrxDataStore,
) {

    suspend fun run(link: DeepLink): Result<AuthResult> {
        val email = mrxDataStore.getUserEmail() ?: return Result.failure(
            exception = Throwable(message = NO_EMAIL_FOUND_MESSAGE)
        )
        val signInLink = link.getSignInLink() ?: return Result.failure(
            exception = Throwable(message = NO_LINK_FOUND_MESSAGE)
        )
        return authRepository.signInWithEmailLink(email = email, link = signInLink)
    }

    companion object {
        private const val NO_EMAIL_FOUND_MESSAGE = "NO_USER_EMAIL_FOUND"
        private const val NO_LINK_FOUND_MESSAGE = "NO_LINK_FOUND"
    }
}
