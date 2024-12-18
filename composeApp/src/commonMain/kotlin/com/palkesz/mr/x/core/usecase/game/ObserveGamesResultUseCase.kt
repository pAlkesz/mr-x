package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.feature.games.GamesResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun interface ObserveGamesResultUseCase {
    fun run(): Flow<GamesResult>
}

class ObserveGamesResultUseCaseImpl(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
    private val barkochbaQuestionRepository: BarkochbaQuestionRepository,
) : ObserveGamesResultUseCase {

    override fun run() = combine(
        gameRepository.games,
        userRepository.users,
        questionRepository.questions,
        barkochbaQuestionRepository.questions,
    ) { games, users, questions, barkochbaQuestions ->
        val hosts = users.filter { user -> user.id in games.map { it.hostId } }
        val gameIds = games.map { it.id }
        GamesResult(
            games = games.filter { game -> game.hostId in hosts.map { it.id } },
            hosts = hosts,
            questions = questions.filter { it.gameId in gameIds },
            barkochbaQuestions = barkochbaQuestions.filter { it.gameId in gameIds },
        )
    }
}
