package com.mr.x.feature.games.gameDetailsScreen

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module


actual val gameDetailsViewModelModule = module {
	viewModelOf(::GamesDetailViewModel)
}
