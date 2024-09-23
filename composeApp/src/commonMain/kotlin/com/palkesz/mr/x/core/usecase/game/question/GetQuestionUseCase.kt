package com.palkesz.mr.x.core.usecase.game.question

import com.palkesz.mr.x.core.data.game.QuestionRepository

class GetQuestionUseCase(
	private val questionRepository: QuestionRepository
) {
	fun run(questionId: String) = questionRepository.getQuestion(questionId)
}
