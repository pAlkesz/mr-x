package com.mr.x.feature.games.question.specify

import com.mr.x.core.usecase.game.question.AcceptHostAnswerUseCase
import com.mr.x.core.usecase.game.question.GetQuestionUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val specifyQuestionModule = module {
	includes(specifyQuestionViewModelModule)
	factory { GetQuestionUseCase(get()) }
	factory { AcceptHostAnswerUseCase(get()) }
}

expect val specifyQuestionViewModelModule: Module