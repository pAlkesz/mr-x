package com.palkesz.mr.x.core.util

import dev.theolm.rinku.Rinku
import dev.theolm.rinku.models.DeepLinkParam
import kotlinx.serialization.builtins.serializer

actual fun createUniversalAppLinkFromGameId(gameId: String) = Rinku.buildUrl(
    DOMAIN,
    DeepLinkParam(GAME_PARAM, gameId, String.serializer())
)

private const val DOMAIN = "mrxapp://"
private const val GAME_PARAM = "game"
