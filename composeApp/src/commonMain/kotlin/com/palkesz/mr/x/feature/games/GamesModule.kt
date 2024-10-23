package com.palkesz.mr.x.feature.games

import com.palkesz.mr.x.core.usecase.game.FetchGamesUseCase
import com.palkesz.mr.x.feature.games.answer.answerQuestionModule
import com.palkesz.mr.x.feature.games.question.barkochba.barkochbaQuestionModule
import com.palkesz.mr.x.feature.games.question.choose.chooseQuestionModule
import com.palkesz.mr.x.feature.games.question.normal.normalQuestionModule
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
        normalQuestionModule,
        chooseQuestionModule,
        barkochbaQuestionModule,
        answerQuestionModule,
        specifyQuestionModule,
    )
}
