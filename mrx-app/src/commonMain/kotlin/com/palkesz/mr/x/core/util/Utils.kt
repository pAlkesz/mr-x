package com.palkesz.mr.x.core.util

expect fun randomUUID(): String

expect fun createUniversalAppLinkFromGameId(gameId: String): String
