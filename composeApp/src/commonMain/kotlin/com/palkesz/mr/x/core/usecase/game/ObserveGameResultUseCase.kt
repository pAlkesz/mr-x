package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.feature.games.game.GameResult
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ObserveGameResultUseCase(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
    private val barkochbaQuestionRepository: BarkochbaQuestionRepository,
) {

    fun run(id: String) = combine(
        gameRepository.games.map { games -> games.first { it.id == id } },
        userRepository.users,
        questionRepository.questions.map { questions -> questions.filter { it.gameId == id } },
        barkochbaQuestionRepository.questions.map { questions -> questions.filter { it.gameId == id } },
    ) { game, users, questions, barkochbaQuestions ->
        val host = users.first { it.id == game.hostId }
        val questionOwnerIds =
            (questions.map { it.userId } + barkochbaQuestions.map { it.userId }).distinct()
        val players = users.filter { user -> user.id in questionOwnerIds }
        val playerIds = players.map { it.id }
        GameResult(
            game = game,
            host = host,
            players = players,
            questions = questions.filter { question ->
                question.userId in playerIds && question.playerAnswer?.userId?.let { userId ->
                    userId in playerIds
                } ?: true
            },
            barkochbaQuestions = barkochbaQuestions.filter { it.userId in playerIds },
        )
    }

}
