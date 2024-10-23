package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.NO_USER_ID_FOUND_MESSAGE
import com.palkesz.mr.x.core.data.game.GameRepository

class FetchGamesUseCase(
    private val gameRepository: GameRepository,
    private val authRepository: AuthRepository
) {

    suspend fun run() = authRepository.userId?.let { userId ->
        gameRepository.fetchGames(playerId = userId)
    } ?: Result.failure(exception = Throwable(NO_USER_ID_FOUND_MESSAGE))

}
