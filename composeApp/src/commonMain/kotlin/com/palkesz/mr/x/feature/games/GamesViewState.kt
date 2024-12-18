package com.palkesz.mr.x.feature.games

import androidx.compose.runtime.Immutable
import com.palkesz.mr.x.core.model.game.Game
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.model.question.BarkochbaQuestion
import com.palkesz.mr.x.core.model.question.Question
import com.palkesz.mr.x.core.model.user.User
import kotlinx.collections.immutable.ImmutableList

data class GamesResult(
    val games: List<Game>,
    val hosts: List<User>,
    val questions: List<Question>,
    val barkochbaQuestions: List<BarkochbaQuestion>,
)

@Immutable
data class GamesViewState(
    val joinedGameId: String?,
    val games: ImmutableList<GameItem>,
    val event: GamesEvent? = null,
)

@Immutable
data class GameItem(
    val id: String,
    val initial: Char,
    val status: GameStatus,
    val isHost: Boolean,
    val hostName: String?,
    val questionCount: Int,
    val barkochbaCount: Int,
)

@Immutable
sealed interface GamesEvent {

    data class NavigateToGame(val id: String) : GamesEvent

}
