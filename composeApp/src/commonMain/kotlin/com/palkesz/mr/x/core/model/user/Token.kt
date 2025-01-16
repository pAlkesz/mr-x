package com.palkesz.mr.x.core.model.user

import kotlinx.serialization.Serializable

@Serializable
data class Token(val userId: String, val token: String, val locale: String)
