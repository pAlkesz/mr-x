package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.question.QuestionRepository

class GetQuestionsOfGameUseCase(
    private val questionRepository: QuestionRepository,
) {
    /*fun run(gameId: String) = questionRepository.getQuestionsOfGame(gameId = gameId)
        .flatMapResult { questions ->
            questionRepository.playerQuestions.mapNotNull { it ->
                Success(it.filter {
                    it.gameId == gameId
                })
            }.prepend(Success(questions))
        }.distinctUntilChanged()*/
}
