package com.mr.x.feature.games.question.choose

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val chooseQuestionViewModelModule = module {
	viewModelOf(::ChooseQuestionViewModelImpl)
}
