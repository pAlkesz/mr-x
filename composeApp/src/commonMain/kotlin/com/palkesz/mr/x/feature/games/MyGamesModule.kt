package com.palkesz.mr.x.feature.games

import com.palkesz.mr.x.core.usecase.game.GetMyGamesUseCase
import com.palkesz.mr.x.feature.games.answer.answerQuestionModule
import com.palkesz.mr.x.feature.games.question.barkochba.barkochbaQuestionModule
import com.palkesz.mr.x.feature.games.question.choose.chooseQuestionModule
import com.palkesz.mr.x.feature.games.question.normal.normalQuestionModule
import com.palkesz.mr.x.feature.games.question.specify.specifyQuestionModule
import org.koin.core.module.Module
import org.koin.dsl.module

val myGamesModule = module {
	factory { GetMyGamesUseCase(get(), get()) }
	includes(
		myGamesViewModelModule,
		normalQuestionModule,
		chooseQuestionModule,
		barkochbaQuestionModule,
		answerQuestionModule,
		specifyQuestionModule
	)
}

expect val myGamesViewModelModule: Module
