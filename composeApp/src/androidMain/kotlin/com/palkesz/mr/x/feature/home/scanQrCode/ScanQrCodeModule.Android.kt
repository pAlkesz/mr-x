package com.palkesz.mr.x.feature.home.scanQrCode

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val scanQrCodeViewModelModule = module {
	viewModelOf(::ScanQrCodeViewModelImpl)
}
