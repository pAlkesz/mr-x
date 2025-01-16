package com.palkesz.mr.x.feature.games

import com.palkesz.mr.x.core.data.auth.AuthRepository
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.util.extensions.getInitial
import com.palkesz.mr.x.core.util.extensions.immutableMapNotNull
import kotlinx.collections.immutable.ImmutableList

fun interface GamesUiMapper {
    fun mapGames(
        result: GamesResult,
        notifications: List<Pair<String, Int>>
    ): ImmutableList<GameItem>
}

class GamesUiMapperImpl(private val authRepository: AuthRepository) : GamesUiMapper {

    override fun mapGames(result: GamesResult, notifications: List<Pair<String, Int>>) =
        result.games.sortedByDescending { game ->
            game.getLastActivityTimeStamp(result = result)
        }.immutableMapNotNull { game ->
            game.getInitial()?.let { initial ->
                val isHost = game.hostId == authRepository.userId
                GameItem(
                    id = game.id,
                    initial = initial.uppercaseChar(),
                    status = game.status,
                    isHost = isHost,
                    hostName = result.hosts.firstOrNull { user ->
                        user.id == game.hostId
                    }?.takeIf { !isHost }?.name,
                    questionCount = result.questions.count { it.gameId == game.id },
                    barkochbaCount = result.barkochbaQuestions.count { it.gameId == game.id },
                    badgeCount = notifications.find { it.first == game.id }?.second?.takeIf { it > 0 },
                )
            }
        }

    private fun Game.getLastActivityTimeStamp(result: GamesResult) =
        (result.questions.filter { it.gameId == id }
            .map { it.lastModifiedTimestamp } + result.barkochbaQuestions.filter { it.gameId == id }
            .map { it.lastModifiedTimestamp }).maxOfOrNull { it.seconds }
}
