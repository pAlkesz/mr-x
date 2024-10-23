package com.palkesz.mr.x.feature.games

import androidx.compose.runtime.Immutable
import com.palkesz.mr.x.core.model.game.GameStatus
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class GamesViewState(val games: ImmutableList<GameItem>, val event: GamesEvent? = null)

@Immutable
data class GameItem(val id: String, val initial: Char, val status: GameStatus, val isHost: Boolean)

@Immutable
sealed interface GamesEvent {

    data class NavigateToGame(val id: String) : GamesEvent

}
