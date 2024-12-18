package com.palkesz.mr.x.feature.games

import com.palkesz.mr.x.core.usecase.game.FetchGamesResultUseCase
import com.palkesz.mr.x.core.usecase.game.FetchGamesResultUseCaseImpl
import com.palkesz.mr.x.core.usecase.game.ObserveGamesResultUseCase
import com.palkesz.mr.x.core.usecase.game.ObserveGamesResultUseCaseImpl
import com.palkesz.mr.x.feature.games.game.gameModule
import com.palkesz.mr.x.feature.games.qrcode.qrCodeModule
import com.palkesz.mr.x.feature.games.question.barkochba.createBarkochbaQuestionModule
import com.palkesz.mr.x.feature.games.question.create.createQuestionModule
import com.palkesz.mr.x.feature.games.question.guess.guessQuestionModule
import com.palkesz.mr.x.feature.games.question.specify.specifyQuestionModule
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val gamesModule = module {
    factoryOf(::FetchGamesResultUseCaseImpl) bind FetchGamesResultUseCase::class
    factoryOf(::ObserveGamesResultUseCaseImpl) bind ObserveGamesResultUseCase::class
    factoryOf(::GamesUiMapperImpl) bind GamesUiMapper::class
    viewModel { parameters ->
        GamesViewModelImpl(parameters.getOrNull(), get(), get(), get())
    } bind GamesViewModel::class
    includes(
        gameModule,
        qrCodeModule,
        createQuestionModule,
        guessQuestionModule,
        specifyQuestionModule,
        createBarkochbaQuestionModule,
    )
}
