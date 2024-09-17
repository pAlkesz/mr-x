package com.palkesz.mr.x.core.model

import kotlinx.serialization.Serializable

@Serializable
data class User(val userId: String, val userName: String)
