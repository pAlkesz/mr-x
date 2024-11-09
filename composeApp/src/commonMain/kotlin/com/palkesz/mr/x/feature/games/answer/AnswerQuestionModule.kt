package com.palkesz.mr.x.feature.games.answer

import com.palkesz.mr.x.core.usecase.game.GetGameAndQuestionUseCase
import com.palkesz.mr.x.core.usecase.question.AnswerQuestionUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val answerQuestionModule = module {
    factory { AnswerQuestionUseCase(get(), get()) }
    factory { GetGameAndQuestionUseCase(get(), get()) }
    includes(answerQuestionViewModelModule)
}

expect val answerQuestionViewModelModule: Module
