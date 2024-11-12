package com.palkesz.mr.x.core.usecase.question

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.NO_USER_ID_FOUND_MESSAGE
import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepository
import com.palkesz.mr.x.core.model.question.BarkochbaQuestion
import com.palkesz.mr.x.core.model.question.BarkochbaStatus
import dev.gitlive.firebase.firestore.Timestamp
import kotlin.uuid.Uuid

class CreateBarkochbaQuestionUseCase(
    private val barkochbaQuestionRepository: BarkochbaQuestionRepository,
    private val authRepository: AuthRepository,
) {

    suspend fun run(gameId: String) = runCatching {
        val userId = authRepository.userId ?: throw Throwable(NO_USER_ID_FOUND_MESSAGE)
        barkochbaQuestionRepository.createQuestion(
            question = BarkochbaQuestion(
                id = Uuid.random().toString(),
                userId = userId,
                gameId = gameId,
                number = barkochbaQuestionRepository.questions.value.filter { it.gameId == gameId }.size + 1,
                text = "",
                status = BarkochbaStatus.IN_STORE,
                answer = false,
                lastModifiedTimestamp = Timestamp.now(),
            )
        ).getOrThrow()
    }
}
