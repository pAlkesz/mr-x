package com.palkesz.mr.x.core.model.question

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: String,
    val userId: String,
    val gameId: String,
    val number: Int,
    val expectedFirstName: String,
    val expectedLastName: String,
    val hostAnswer: Answer?,
    val playerAnswer: Answer?,
    val status: QuestionStatus,
    val text: String,
    val lastModifiedTimestamp: Timestamp,
)

@Suppress("Unused")
@Serializable
enum class QuestionStatus {
    WAITING_FOR_HOST,
    WAITING_FOR_PLAYERS,
    WAITING_FOR_OWNER,
    GUESSED_BY_PLAYER,
    MISSED_BY_PLAYER,
    GUESSED_BY_HOST,
    PLAYERS_WON,
}
