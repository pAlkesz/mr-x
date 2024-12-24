package com.palkesz.mr.x.feature.games.question.barkochba

import com.palkesz.mr.x.core.usecase.question.CreateBarkochbaQuestionUseCase
import com.palkesz.mr.x.core.usecase.question.UpdateBarkochbaQuestionUseCase
import com.palkesz.mr.x.core.usecase.question.UpdateBarkochbaQuestionUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val createBarkochbaQuestionModule = module {
    viewModel { parameters ->
        CreateBarkochbaQuestionViewModelImpl(parameters.get(), get(), get(), get())
    } bind CreateBarkochbaQuestionViewModel::class
    factoryOf(::CreateBarkochbaQuestionUseCase)
    factoryOf(::UpdateBarkochbaQuestionUseCaseImpl) bind UpdateBarkochbaQuestionUseCase::class
}
