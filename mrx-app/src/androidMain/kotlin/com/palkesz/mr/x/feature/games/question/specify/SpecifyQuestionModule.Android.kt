package com.palkesz.mr.x.feature.games.question.specify

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val specifyQuestionViewModelModule = module {
	viewModelOf(::SpecifyQuestionViewModelImpl)
}
