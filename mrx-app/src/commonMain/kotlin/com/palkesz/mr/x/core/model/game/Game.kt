package com.palkesz.mr.x.core.model.game

import kotlinx.serialization.Serializable

@Serializable
data class Game(
	val uuid: String,
	val firstName: String,
	val lastName: String,
	val hostId: String,
	val status: GameStatus
)

@Serializable
enum class GameStatus {
	ONGOING,
	FINISHED
}
