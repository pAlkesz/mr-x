package com.mr.x.core.usecase.game

import com.mr.x.core.data.game.QuestionRepository
import com.mr.x.core.util.combineResultPair

class GetGameAndQuestionUseCase(
	private val questionRepository: QuestionRepository,
	private val getAndObserveGameUseCase: GetAndObserveGameUseCase
) {

	fun run(gameId: String, questionId: String) = getAndObserveGameUseCase.run(gameId)
		.combineResultPair(questionRepository.getQuestion(questionId))
}
