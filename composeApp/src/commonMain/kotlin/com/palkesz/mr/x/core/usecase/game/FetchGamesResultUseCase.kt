package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.NO_USER_ID_FOUND_MESSAGE
import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.question.BarkochbaQuestionRepository
import com.palkesz.mr.x.core.data.question.QuestionRepository
import com.palkesz.mr.x.core.data.user.UserRepository
import com.palkesz.mr.x.feature.games.GamesResult

fun interface FetchGamesResultUseCase {
    suspend fun run(): Result<GamesResult>
}

class FetchGamesResultUseCaseImpl(
    private val gameRepository: GameRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
    private val barkochbaQuestionRepository: BarkochbaQuestionRepository,
) : FetchGamesResultUseCase {

    override suspend fun run() = runCatching {
        val userId = authRepository.userId ?: throw Throwable(NO_USER_ID_FOUND_MESSAGE)
        val games = gameRepository.fetchGames(playerId = userId).getOrThrow()
        val gameIds = games.map { it.id }
        val hosts = userRepository.fetchUsers(ids = games.map { it.hostId }).getOrThrow()
        val questions = questionRepository.fetchQuestions(gameIds = gameIds).getOrThrow()
        val barkochbaQuestions =
            barkochbaQuestionRepository.fetchQuestions(gameIds = gameIds).getOrThrow()
        GamesResult(
            games = games,
            hosts = hosts,
            questions = questions,
            barkochbaQuestions = barkochbaQuestions,
        )
    }
}
