package com.palkesz.mr.x.feature.games.game

import com.palkesz.mr.x.core.usecase.game.FetchGameResultUseCase
import com.palkesz.mr.x.core.usecase.game.ObserveGameResultUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val gameModule = module {
    factoryOf(::GameUiMapper)
    factoryOf(::FetchGameResultUseCase)
    factoryOf(::ObserveGameResultUseCase)
    viewModel { parameters ->
        GameViewModelImpl(parameters.get(), get(), get(), get(), get(), get())
    } bind GameViewModel::class
}
