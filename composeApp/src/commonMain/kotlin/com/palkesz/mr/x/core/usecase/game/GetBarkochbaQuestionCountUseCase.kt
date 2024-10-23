package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.model.question.Status
import com.palkesz.mr.x.core.util.extensions.mapResult

class GetBarkochbaQuestionCountUseCase(
    private val questionRepository: QuestionRepository,
    private val authRepository: AuthRepository
) {
    fun run(gameId: String) =
        questionRepository.getQuestionsOfGame(gameId).mapResult {
            it.count { question ->
                question.askerId == authRepository.userId
                        && question.status == Status.CORRECT_ANSWER
            }
        }

}
