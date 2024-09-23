package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.data.user.AuthRepository
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.util.extensions.flatMapResult
import com.palkesz.mr.x.core.util.extensions.prepend
import com.palkesz.mr.x.core.util.networking.Error
import com.palkesz.mr.x.core.util.networking.Result
import com.palkesz.mr.x.core.util.networking.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull

class GetMyGamesUseCase(
    private val gameRepository: GameRepository,
    private val authRepository: AuthRepository
) {

    fun run(): Flow<Result<List<Game>>> =
        authRepository.currentUserId?.let { userId ->
            gameRepository.getPlayerGames(playerId = userId).flatMapResult { games ->
                gameRepository.playerGames.mapNotNull {
                    Success(it)
                }.prepend(Success(games))
            }.distinctUntilChanged()
        } ?: flowOf(Error(IllegalStateException()))
}
