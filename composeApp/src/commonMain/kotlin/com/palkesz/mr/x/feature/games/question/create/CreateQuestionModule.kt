package com.palkesz.mr.x.feature.games.question.create

import com.palkesz.mr.x.core.usecase.question.CreateQuestionUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val createQuestionModule = module {
    viewModel { parameters ->
        CreateQuestionViewModelImpl(parameters.get(), get(), get(), get())
    } bind CreateQuestionViewModel::class
    factoryOf(::CreateQuestionUseCase)
}
