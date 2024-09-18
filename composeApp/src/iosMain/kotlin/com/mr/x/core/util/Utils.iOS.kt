package com.mr.x.core.util

import dev.theolm.rinku.Rinku
import dev.theolm.rinku.models.DeepLinkParam
import kotlinx.serialization.builtins.serializer
import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()

actual fun createUniversalAppLinkFromGameId(gameId: String) = Rinku.buildUrl(
	DOMAIN,
	DeepLinkParam(GAME_PARAM, gameId, String.serializer()))

private const val DOMAIN = "mrxapp://"
private const val GAME_PARAM = "game"
