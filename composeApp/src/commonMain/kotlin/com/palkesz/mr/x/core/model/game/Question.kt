package com.palkesz.mr.x.core.model.game

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class Question(
	val hostAnswer: Answer? = null,
	val playerAnswer: Answer? = null,
	val barkochbaText: String = "",
	val barkochbaAnswer: Boolean? = null,
	val askerId: String,
	val expectedFirstName: String,
	val expectedLastName: String,
	val gameId: String,
	val status: Status,
	val text: String,
	val uuid: String,
	val lastModifiedTS: Timestamp
)

@Serializable
data class Answer(
	val firstName: String = "",
	val lastName: String = "",
	val userId: String = ""
)

@Serializable
enum class Status {
	WAITING_FOR_HOST,
	WAITING_FOR_PLAYERS,
	WAITING_FOR_OWNER,
	CORRECT_ANSWER,
	WRONG_ANSWER,
	BARKOCHBA_ASKED,
	BARKOCHBA_ANSWERED,
	GUESSED_BY_HOST,
	PLAYER_WON
}
