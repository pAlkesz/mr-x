package com.palkesz.mr.x.feature.games.game

import com.palkesz.mr.x.core.usecase.game.FetchGameResultUseCase
import com.palkesz.mr.x.core.usecase.game.ObserveGameResultUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val gameModule = module {
    factoryOf(::GameUiMapperImpl) bind GameUiMapper::class
    factoryOf(::FetchGameResultUseCase)
    factoryOf(::ObserveGameResultUseCase)
    viewModel { parameters ->
        GameViewModelImpl(
            parameters.get(i = 0),
            parameters.get(i = 1),
            parameters.get(i = 2),
            parameters.get(i = 3),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    } bind GameViewModel::class
}
