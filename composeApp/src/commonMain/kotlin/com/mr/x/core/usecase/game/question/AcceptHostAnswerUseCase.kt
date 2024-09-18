package com.mr.x.core.usecase.game.question

import com.mr.x.core.data.game.QuestionRepository
import com.mr.x.core.util.CoroutineHelper
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
