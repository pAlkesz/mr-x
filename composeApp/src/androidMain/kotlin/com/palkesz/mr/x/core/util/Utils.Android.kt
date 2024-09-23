package com.palkesz.mr.x.core.util

actual fun createUniversalAppLinkFromGameId(gameId: String) = APP_URL + gameId

private const val APP_URL = "https://mrxapp/"
