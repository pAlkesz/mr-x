package com.palkesz.mr.x.core.model.question

import kotlinx.serialization.Serializable

@Serializable
data class Answer(val firstName: String, val lastName: String?, val userId: String)
