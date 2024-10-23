package com.palkesz.mr.x.feature.home.create

import com.palkesz.mr.x.core.usecase.game.CreateGameUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val createGameModule = module {
    factoryOf(::CreateGameUseCase)
    viewModelOf(::CreateGameViewModelImpl) bind CreateGameViewModel::class
}
