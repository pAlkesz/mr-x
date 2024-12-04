package com.palkesz.mr.x.feature.games.question.guess

import com.palkesz.mr.x.core.usecase.question.GuessQuestionUseCase
import com.palkesz.mr.x.core.usecase.question.GuessQuestionUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val guessQuestionModule = module {
    factoryOf(::GuessQuestionUseCaseImpl) bind GuessQuestionUseCase::class
    viewModel { parameters ->
        GuessQuestionViewModelImpl(
            parameters.get(i = 0),
            parameters.get(i = 1),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    } bind GuessQuestionViewModel::class
}
