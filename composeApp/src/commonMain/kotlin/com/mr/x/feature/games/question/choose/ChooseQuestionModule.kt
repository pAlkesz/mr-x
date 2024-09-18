package com.mr.x.feature.games.question.choose

import com.mr.x.core.usecase.game.GetBarkochbaQuestionCountUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val chooseQuestionModule = module {
	includes(chooseQuestionViewModelModule)
	factory { GetBarkochbaQuestionCountUseCase(get(), get()) }
}

expect val chooseQuestionViewModelModule: Module
