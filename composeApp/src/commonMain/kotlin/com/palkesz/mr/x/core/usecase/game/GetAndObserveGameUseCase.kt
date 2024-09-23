package com.palkesz.mr.x.core.usecase.game

import com.palkesz.mr.x.core.data.game.GameRepository
import com.palkesz.mr.x.core.util.extensions.flatMapResult
import com.palkesz.mr.x.core.util.extensions.prepend
import com.palkesz.mr.x.core.util.networking.Success
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

class GetAndObserveGameUseCase(private val gameRepository: GameRepository) {

    fun run(gameId: String) = gameRepository.getGame(gameId).flatMapResult { game ->
        gameRepository.playerGames.mapNotNull {
            Success(it.first { it.uuid == gameId })
        }.prepend(Success(game))
    }.distinctUntilChanged()
}
