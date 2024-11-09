package com.palkesz.mr.x.core.model.game

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: String,
    val firstName: String,
    val lastName: String?,
    val hostId: String,
    val status: GameStatus,
    val lastModifiedTimestamp: Timestamp,
)

@Suppress("Unused")
@Serializable
enum class GameStatus { ONGOING, FINISHED }
