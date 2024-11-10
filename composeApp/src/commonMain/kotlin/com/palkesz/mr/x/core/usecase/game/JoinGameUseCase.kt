package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.data.auth.AuthRepositoryImpl.Companion.NO_USER_ID_FOUND_MESSAGE
import com.palkesz.mr.x.core.data.game.GameRepository

class JoinGameUseCase(
    private val gameRepository: GameRepository,
    private val authRepository: AuthRepository,
) {

    suspend fun run(gameId: String) = authRepository.userId?.let { userId ->
        gameRepository.joinGame(gameId = gameId, playerId = userId)
    } ?: Result.failure(Throwable(NO_USER_ID_FOUND_MESSAGE))
}
