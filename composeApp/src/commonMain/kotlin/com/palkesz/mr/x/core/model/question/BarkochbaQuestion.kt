package com.palkesz.mr.x.core.model.question

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class BarkochbaQuestion(
    val id: String,
    val gameId: String,
    val userId: String,
    val status: BarkochbaStatus,
    val number: Int,
    val text: String,
    val answer: Boolean,
    val lastModifiedTimestamp: Timestamp,
)

@Serializable
enum class BarkochbaStatus { IN_STORE, ASKED, ANSWERED }
