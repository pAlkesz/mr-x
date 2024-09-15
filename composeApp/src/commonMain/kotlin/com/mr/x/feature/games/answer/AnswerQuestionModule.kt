package com.mr.x.feature.games.answer

import com.mr.x.core.usecase.game.GetAndObserveGameUseCase
import com.mr.x.core.usecase.game.GetGameAndQuestionUseCase
import com.mr.x.core.usecase.game.question.AnswerQuestionUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val answerQuestionModule = module {
	factory { AnswerQuestionUseCase(get(), get()) }
	factory { GetAndObserveGameUseCase(get()) }
	factory { GetGameAndQuestionUseCase(get(), get()) }
	includes(answerQuestionViewModelModule)
}

expect val answerQuestionViewModelModule: Module
