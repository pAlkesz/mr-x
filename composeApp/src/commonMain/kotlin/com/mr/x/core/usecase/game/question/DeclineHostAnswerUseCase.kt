package com.mr.x.core.usecase.game.question

import com.mr.x.core.data.game.QuestionRepository
import com.mr.x.core.model.game.Status
import com.mr.x.core.util.CoroutineHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DeclineHostAnswerUseCase(
	private val questionRepository: QuestionRepository
) {

	fun run(questionId: String) {
		CoroutineHelper.ioScope.launch {
			questionRepository.updateStatus(
				uuid = questionId,
				status = Status.WAITING_FOR_PLAYERS
			).collect()
		}
	}
}
