package com.palkesz.mr.x.feature.games.question.normal

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val normalQuestionViewModelModule = module {
	viewModelOf(::NormalQuestionViewModelImpl)
}
