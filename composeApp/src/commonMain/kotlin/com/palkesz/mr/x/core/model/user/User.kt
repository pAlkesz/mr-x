package com.palkesz.mr.x.core.model.user

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: String, val name: String)
