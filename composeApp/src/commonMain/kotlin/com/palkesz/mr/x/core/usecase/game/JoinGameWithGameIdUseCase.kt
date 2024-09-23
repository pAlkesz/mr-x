package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.user.AuthRepository
import com.palkesz.mr.x.core.util.coroutines.CoroutineHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class JoinGameWithGameIdUseCase(
    private val gameRepository: GameRepository,
    private val authRepository: AuthRepository
) {
    fun run(gameId: String) {
        authRepository.currentUserId?.let { userId ->
            CoroutineHelper.mainScope.launch {
                gameRepository.joinGameWithGameId(gameId = gameId, playerId = userId).collect()
            }
        }
    }
}
