package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.game.QuestionRepository
import com.palkesz.mr.x.core.data.user.AuthRepository
import com.palkesz.mr.x.core.model.game.Status
import com.palkesz.mr.x.core.util.mapResult

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
