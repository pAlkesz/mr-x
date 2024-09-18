package com.mr.x.core.usecase.game

import com.mr.x.core.data.game.QuestionRepository
import com.mr.x.core.model.game.Status
import com.mr.x.core.util.CoroutineHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PassQuestionAsHostUseCase(
	private val questionRepository: QuestionRepository
) {
	fun run(questionId: String) {
		CoroutineHelper.ioScope.launch {
			val status = Status.WAITING_FOR_PLAYERS
			questionRepository.updateStatus(questionId, status).collect()
		}
	}
}
