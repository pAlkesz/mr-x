package com.palkesz.mr.x.feature.games

import com.palkesz.mr.x.core.usecase.game.FetchGamesUseCase
import com.palkesz.mr.x.feature.games.game.gameModule
import com.palkesz.mr.x.feature.games.qrcode.qrCodeModule
import com.palkesz.mr.x.feature.games.question.barkochba.barkochbaQuestionModule
import com.palkesz.mr.x.feature.games.question.create.createQuestionModule
import com.palkesz.mr.x.feature.games.question.guess.guessQuestionModule
import com.palkesz.mr.x.feature.games.question.specify.specifyQuestionModule
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val gamesModule = module {
    factoryOf(::FetchGamesUseCase)
    factoryOf(::GamesUiMapper)
    viewModelOf(::GamesViewModelImpl) bind GamesViewModel::class
    includes(
        gameModule,
        qrCodeModule,
        createQuestionModule,
        guessQuestionModule,
        specifyQuestionModule,
        barkochbaQuestionModule,
    )
}
