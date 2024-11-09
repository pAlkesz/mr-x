package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.question.QuestionRepository

class AnswerBarkochbaQuestionUseCase(
    private val questionRepository: QuestionRepository,
) {
    /*fun run(answer: Boolean, questionId: String) {
        CoroutineHelper.ioScope.launch {
            val status = Status.BARKOCHBA_ANSWERED
            questionRepository.updateBarkochbaQuestion(answer, questionId, status).collect()
        }
    }*/
}
