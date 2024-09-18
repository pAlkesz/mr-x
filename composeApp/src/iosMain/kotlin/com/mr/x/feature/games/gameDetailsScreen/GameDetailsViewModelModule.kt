package com.mr.x.feature.games.gameDetailsScreen

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val gameDetailsViewModelModule = module {
	factoryOf(::GamesDetailViewModel)
}
