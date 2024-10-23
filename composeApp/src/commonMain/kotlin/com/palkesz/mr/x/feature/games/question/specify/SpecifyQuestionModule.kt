package com.palkesz.mr.x.feature.games.question.specify

import com.palkesz.mr.x.core.usecase.question.AcceptHostAnswerUseCase
import com.palkesz.mr.x.core.usecase.question.GetQuestionUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val specifyQuestionModule = module {
	includes(specifyQuestionViewModelModule)
	factory { GetQuestionUseCase(get()) }
	factory { AcceptHostAnswerUseCase(get()) }
}

expect val specifyQuestionViewModelModule: Module
