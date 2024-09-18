package com.mr.x.feature.games.question.normal

import com.mr.x.core.usecase.game.UploadNormalQuestionUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val normalQuestionModule = module {
	includes(normalQuestionViewModelModule)
	factory { UploadNormalQuestionUseCase(get(), get(), get()) }
}

expect val normalQuestionViewModelModule: Module
