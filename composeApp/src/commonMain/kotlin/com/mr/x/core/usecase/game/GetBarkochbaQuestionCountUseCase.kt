package com.mr.x.core.usecase.game

import com.mr.x.core.data.game.QuestionRepository
import com.mr.x.core.data.user.AuthRepository
import com.mr.x.core.model.game.Status
import com.mr.x.core.util.mapResult

class GetBarkochbaQuestionCountUseCase(
	private val questionRepository: QuestionRepository,
	private val authRepository: AuthRepository
) {
	fun run(gameId: String) =
		questionRepository.getQuestionsOfGame(gameId).mapResult {
			it.count { question ->
				question.askerId == authRepository.currentUserId
						&& question.status == Status.CORRECT_ANSWER
			}
		}

}