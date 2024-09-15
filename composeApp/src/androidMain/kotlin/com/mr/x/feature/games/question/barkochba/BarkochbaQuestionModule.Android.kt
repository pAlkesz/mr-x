package com.mr.x.feature.games.question.barkochba

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val barkochbaQuestionViewModelModule = module {
	viewModelOf(::BarkochbaQuestionViewModelImpl)
}
