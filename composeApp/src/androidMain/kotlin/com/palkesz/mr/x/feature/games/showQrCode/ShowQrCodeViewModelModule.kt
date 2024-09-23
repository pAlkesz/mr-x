package com.palkesz.mr.x.feature.games.showQrCode

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val showQrCodeViewModelModule = module {
	factoryOf(::ShowShareSheetHelper)
	viewModelOf(::ShowQrCodeViewModelImpl)
}