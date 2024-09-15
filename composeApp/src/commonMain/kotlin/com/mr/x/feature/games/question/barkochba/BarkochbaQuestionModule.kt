package com.mr.x.feature.games.question.barkochba

import com.mr.x.core.usecase.game.UploadBarkochbaQuestionUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val barkochbaQuestionModule = module {
	includes(barkochbaQuestionViewModelModule)
	factory { UploadBarkochbaQuestionUseCase(get(), get()) }
}

expect val barkochbaQuestionViewModelModule: Module
