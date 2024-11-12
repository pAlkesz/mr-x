package com.palkesz.mr.x.feature.games.question.specify

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val specifyQuestionModule = module {
    viewModel { parameters ->
        SpecifyQuestionViewModelImpl(
            parameters.get(i = 0),
            parameters.get(i = 1),
            get(),
            get(),
            get(),
            get(),
        )
    } bind SpecifyQuestionViewModel::class
}
