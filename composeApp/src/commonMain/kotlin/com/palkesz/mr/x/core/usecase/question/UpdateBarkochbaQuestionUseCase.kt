package com.palkesz.mr.x.core.usecase.question

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepository
import com.palkesz.mr.x.core.model.question.BarkochbaStatus

class UpdateBarkochbaQuestionUseCase(
    private val authRepository: AuthRepository,
    private val barkochbaQuestionRepository: BarkochbaQuestionRepository,
) {

    suspend fun run(gameId: String, text: String): Result<Unit> {
        val question = barkochbaQuestionRepository.questions.value.filter { question ->
            question.gameId == gameId && question.userId == authRepository.userId && question.status == BarkochbaStatus.IN_STORE
        }.minByOrNull { question ->
            question.number
        } ?: return Result.failure(exception = Throwable(NO_QUESTION_FOUND_ERROR_MESSAGE))
        return barkochbaQuestionRepository.updateText(id = question.id, text = text)
    }

    companion object {
        private const val NO_QUESTION_FOUND_ERROR_MESSAGE = "NO_BARKOCHBA_QUESTION_FOUND"
    }
}
