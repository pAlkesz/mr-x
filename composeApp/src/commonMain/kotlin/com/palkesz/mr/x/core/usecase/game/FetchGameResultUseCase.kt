package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.feature.games.game.GameResult

class FetchGameResultUseCase(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
    private val barkochbaQuestionRepository: BarkochbaQuestionRepository,
) {

    suspend fun run(id: String) = runCatching {
        val game = gameRepository.fetchGame(id = id).getOrThrow()
        val host = userRepository.fetchUser(id = game.hostId).getOrThrow()
        val questions = questionRepository.fetchQuestions(gameId = id).getOrThrow()
        val barkochbaQuestions =
            barkochbaQuestionRepository.fetchQuestions(gameId = id).getOrThrow()
        val playerIds = (questions.map { question ->
            listOfNotNull(question.userId, question.playerAnswer?.userId)
        }.flatten() + barkochbaQuestions.map { it.userId }).distinct()
        val players = userRepository.fetchUsers(ids = playerIds).getOrThrow()
        GameResult(
            game = game,
            host = host,
            questions = questions,
            barkochbaQuestions = barkochbaQuestions,
            players = players
        )
    }
}
