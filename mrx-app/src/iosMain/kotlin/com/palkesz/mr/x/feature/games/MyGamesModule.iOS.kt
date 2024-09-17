package com.palkesz.mr.x.feature.games

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val myGamesViewModelModule = module {
	factoryOf(::MyGamesViewModelImpl)
}
