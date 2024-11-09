package com.palkesz.mr.x.core.usecase.question

import com.palkesz.mr.x.core.data.question.QuestionRepository

class AcceptHostAnswerUseCase(
    private val questionRepository: QuestionRepository
) {

    fun run(questionId: String, text: String) {
        /*CoroutineHelper.ioScope.launch {
            questionRepository.updateQuestionText(uuid = questionId, text = text.trim()).collect()
        }*/
    }
}
