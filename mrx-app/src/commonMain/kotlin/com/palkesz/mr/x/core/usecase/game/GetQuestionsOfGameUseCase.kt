package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.game.QuestionRepository
import com.palkesz.mr.x.core.util.Success
import com.palkesz.mr.x.core.util.flatMapResult
import com.palkesz.mr.x.core.util.prepend
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

class GetQuestionsOfGameUseCase(
	private val questionRepository: QuestionRepository,
) {
	fun run(gameId: String) = questionRepository.getQuestionsOfGame(gameId = gameId)
		.flatMapResult { questions ->
			questionRepository.playerQuestions.mapNotNull { it ->
				Success(it.filter {
					it.gameId == gameId
				})
			}.prepend(Success(questions))
		}.distinctUntilChanged()
}
