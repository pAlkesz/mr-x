package com.palkesz.mr.x.feature.games

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.util.extensions.getInitial
import com.palkesz.mr.x.core.util.extensions.immutableMapNotNull

class GamesUiMapper(private val authRepository: AuthRepository) {

    fun mapGames(games: List<Game>) = games.immutableMapNotNull { game ->
        game.getInitial()?.let { initial ->
            GameItem(
                id = game.id,
                initial = initial.uppercaseChar(),
                status = game.status,
                isHost = game.hostId == authRepository.userId,
            )
        }
    }

}
