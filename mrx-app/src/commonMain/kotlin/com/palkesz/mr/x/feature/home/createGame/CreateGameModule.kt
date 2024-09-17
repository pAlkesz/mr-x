package com.palkesz.mr.x.feature.home.createGame

import com.palkesz.mr.x.core.usecase.game.CreateGameUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val createGameModule = module {
	factory { CreateGameUseCase(get(), get()) }
	includes(createGameViewModelModule)
}

expect val createGameViewModelModule: Module
