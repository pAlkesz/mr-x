package com.palkesz.mr.x.feature.games.answer

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val answerQuestionViewModelModule = module {
	factoryOf(::AnswerQuestionViewModelImpl)
}
