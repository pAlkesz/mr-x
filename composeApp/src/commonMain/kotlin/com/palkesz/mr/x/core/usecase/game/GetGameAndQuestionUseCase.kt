package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.question.QuestionRepository

class GetGameAndQuestionUseCase(
    private val questionRepository: QuestionRepository,
    private val getAndObserveGameUseCase: GetAndObserveGameUseCase
) {

    /*fun run(gameId: String, questionId: String) = getAndObserveGameUseCase.run(gameId)
        .combineResultPair(questionRepository.getQuestion(questionId))*/
}
