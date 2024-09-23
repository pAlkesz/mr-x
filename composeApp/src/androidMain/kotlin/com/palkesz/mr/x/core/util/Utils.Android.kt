package com.palkesz.mr.x.core.util

import java.util.UUID

actual fun randomUUID() = UUID.randomUUID().toString()

actual fun createUniversalAppLinkFromGameId(gameId: String) = APP_URL + gameId

private const val APP_URL = "https://mrxapp/"
