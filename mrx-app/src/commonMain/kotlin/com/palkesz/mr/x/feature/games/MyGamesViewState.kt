package com.palkesz.mr.x.feature.games

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.palkesz.mr.x.core.model.game.GameStatus
import com.palkesz.mr.x.core.ui.components.ContainsText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import mrx.mrx_app.generated.resources.*
import org.jetbrains.compose.resources.StringResource

@Stable
data class MyGamesViewState(
	val userGames: ImmutableList<GameItem> = persistentListOf(),
	val selectedFilters: ImmutableList<MyGamesFilter> = persistentListOf(),
	val event: GameClickedEvent? = null
)

@Immutable
data class GameItem(
	val initial: Char,
	val name: String?,
	val uuid: String,
	val status: GameStatus,
	val isHost: Boolean
)

sealed interface GameClickedEvent {
	data class GameClicked(val uuid: String) : GameClickedEvent
	data class ShowQrCodeClicked(val uuid: String) : GameClickedEvent
}

enum class MyGamesFilter(override val text: StringResource) : ContainsText {
	ONGOING(Res.string.game_filter_ongoing),
	FINISHED(Res.string.game_filter_finished),
	IS_HOST(Res.string.game_filter_is_host),
	IS_PLAYER(Res.string.game_filter_is_player)
}

fun GameItem.isFilteredOut(filters: List<MyGamesFilter>) = filters.isEmpty() ||
		(this.status == GameStatus.ONGOING && filters.contains(MyGamesFilter.ONGOING)) ||
		(this.status == GameStatus.FINISHED && filters.contains(MyGamesFilter.FINISHED)) ||
		(this.isHost && filters.contains(MyGamesFilter.IS_HOST)) ||
		(this.isHost.not() && filters.contains(MyGamesFilter.IS_PLAYER))

