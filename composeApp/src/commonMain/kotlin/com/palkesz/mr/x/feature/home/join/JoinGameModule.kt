package com.palkesz.mr.x.feature.home.join

import com.palkesz.mr.x.core.usecase.game.JoinGameWithGameIdUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val joinGameModule = module {
    factoryOf(::JoinGameWithGameIdUseCase)
    viewModelOf(::JoinGameViewModelImpl) bind JoinGameViewModel::class
}
