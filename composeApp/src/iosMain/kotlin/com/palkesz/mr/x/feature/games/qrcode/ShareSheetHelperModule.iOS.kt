package com.palkesz.mr.x.feature.games.qrcode

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val shareSheetHelperModule = module {
    factoryOf(::IOSShareSheetHelper) bind ShareSheetHelper::class
}
