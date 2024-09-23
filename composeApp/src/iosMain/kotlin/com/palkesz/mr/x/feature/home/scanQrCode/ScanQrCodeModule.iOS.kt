package com.palkesz.mr.x.feature.home.scanQrCode

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val scanQrCodeViewModelModule = module {
	factoryOf(::ScanQrCodeViewModelImpl)
}
