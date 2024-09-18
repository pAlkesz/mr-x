package com.mr.x.feature.games

import com.mr.x.core.usecase.game.GetMyGamesUseCase
import com.mr.x.feature.games.answer.answerQuestionModule
import com.mr.x.feature.games.question.barkochba.barkochbaQuestionModule
import com.mr.x.feature.games.question.choose.chooseQuestionModule
import com.mr.x.feature.games.question.normal.normalQuestionModule
import com.mr.x.feature.games.question.specify.specifyQuestionModule
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
