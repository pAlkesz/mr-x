package com.palkesz.mr.x.core.usecase.question

import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.util.coroutines.CoroutineHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AcceptHostAnswerUseCase(
    private val questionRepository: QuestionRepository
) {

    fun run(questionId: String, text: String) {
        CoroutineHelper.ioScope.launch {
            questionRepository.updateQuestionText(uuid = questionId, text = text.trim()).collect()
        }
    }
}
