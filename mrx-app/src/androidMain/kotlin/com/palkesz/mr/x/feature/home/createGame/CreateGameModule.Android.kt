package com.palkesz.mr.x.feature.home.createGame

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val createGameViewModelModule = module {
	viewModelOf(::CreateGameViewModelImpl)
}
