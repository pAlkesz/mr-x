package com.palkesz.mr.x.feature.games.answer

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val answerQuestionViewModelModule = module {
	viewModelOf((::AnswerQuestionViewModelImpl))
}
