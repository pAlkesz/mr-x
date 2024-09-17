package com.palkesz.mr.x.feature.games

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val myGamesViewModelModule = module {
	viewModelOf(::MyGamesViewModelImpl)
}
