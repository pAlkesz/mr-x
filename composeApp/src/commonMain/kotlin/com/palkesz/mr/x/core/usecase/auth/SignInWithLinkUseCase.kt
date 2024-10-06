package com.palkesz.mr.x.core.usecase.auth

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.AUTH_TAG
import com.palkesz.mr.x.core.data.datastore.MrxDataStore
import com.palkesz.mr.x.core.util.extensions.getSignInLink
import dev.theolm.rinku.DeepLink
import io.github.aakira.napier.Napier

class SignInWithLinkUseCase(
    private val authRepository: AuthRepository,
    private val mrxDataStore: MrxDataStore,
) {

    suspend fun run(link: DeepLink): Result<Unit> {
        val email = mrxDataStore.getUserEmail() ?: return Result.failure(
            exception = Throwable(message = NO_EMAIL_FOUND_MESSAGE)
        )
        val signInLink = link.getSignInLink() ?: return Result.failure(
            exception = Throwable(message = NO_LINK_FOUND_MESSAGE)
        )
        return authRepository.signInWithEmailLink(email = email, link = signInLink).also {
            Napier.d(tag = AUTH_TAG) { "Signing in with link: $it" }
        }.map { }
    }

    companion object {
        private const val NO_EMAIL_FOUND_MESSAGE = "NO_USER_EMAIL_FOUND"
        private const val NO_LINK_FOUND_MESSAGE = "NO_LINK_FOUND"
    }
}
