package com.mr.x.feature.games.showQrCode

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val showQrCodeViewModelModule = module {
	factoryOf(::ShowShareSheetHelper)
	factoryOf(::ShowQrCodeViewModelImpl)
}