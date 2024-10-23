package com.palkesz.mr.x.core.usecase.question

import com.palkesz.mr.x.core.data.question.QuestionRepository

class GetQuestionUseCase(
	private val questionRepository: QuestionRepository
) {
	fun run(questionId: String) = questionRepository.getQuestion(questionId)
}
