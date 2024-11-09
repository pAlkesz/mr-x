package com.palkesz.mr.x.core.usecase.question

import com.palkesz.mr.x.core.data.question.QuestionRepository

class DeclineHostAnswerUseCase(
    private val questionRepository: QuestionRepository
) {

    fun run(questionId: String) {
        /*CoroutineHelper.ioScope.launch {
            questionRepository.updateStatus(
                uuid = questionId,
                status = Status.WAITING_FOR_PLAYERS
            ).collect()
        }*/
    }
}
