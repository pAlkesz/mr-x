package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.question.QuestionRepository

class PassQuestionAsHostUseCase(
    private val questionRepository: QuestionRepository
) {
    /*fun run(questionId: String) {
        CoroutineHelper.ioScope.launch {
            val status = Status.WAITING_FOR_PLAYERS
            questionRepository.updateStatus(questionId, status).collect()
        }
    }*/
}
