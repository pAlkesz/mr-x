package com.palkesz.mr.x.feature.home.scanQrCode

import com.palkesz.mr.x.core.usecase.game.JoinGameWithGameIdUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val scanQrCodeModule = module {
	factoryOf(::JoinGameWithGameIdUseCase)
	includes(scanQrCodeViewModelModule)
}

expect val scanQrCodeViewModelModule: Module