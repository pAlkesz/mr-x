package com.palkesz.mr.x.feature.home.createGame

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val createGameViewModelModule = module {
	factoryOf(::CreateGameViewModelImpl)
}
