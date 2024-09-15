package com.mr.x.feature.games.question.choose

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val chooseQuestionViewModelModule = module {
	factoryOf(::ChooseQuestionViewModelImpl)
}
