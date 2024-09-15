package com.mr.x.feature.games.showQrCode

import org.koin.core.module.Module
import org.koin.dsl.module

val showQrCodeModule = module {
	includes(showQrCodeViewModelModule)
}

expect val showQrCodeViewModelModule: Module