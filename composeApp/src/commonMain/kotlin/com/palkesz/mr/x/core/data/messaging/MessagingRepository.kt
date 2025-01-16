package com.palkesz.mr.x.core.data.messaging

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.NO_USER_ID_FOUND_MESSAGE
import com.palkesz.mr.x.core.model.user.Token
import com.palkesz.mr.x.core.util.platform.locale
import dev.gitlive.firebase.firestore.FirebaseFirestore

interface MessagingRepository {
    suspend fun uploadToken(token: String): Result<Unit>
}

class MessagingRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) : MessagingRepository {

    override suspend fun uploadToken(token: String) = try {
        val userId = authRepository.userId ?: throw Throwable(message = NO_USER_ID_FOUND_MESSAGE)
        firestore.collection(TOKENS_COLLECTION_KEY)
            .document(userId)
            .set(Token(userId = userId, token = token, locale = locale))
        Result.success(value = Unit)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    companion object {
        private const val TOKENS_COLLECTION_KEY = "tokens"
    }
}
