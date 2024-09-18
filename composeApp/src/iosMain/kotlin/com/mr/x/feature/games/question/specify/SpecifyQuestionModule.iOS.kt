package com.mr.x.feature.games.question.specify

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val specifyQuestionViewModelModule = module {
	factoryOf(::SpecifyQuestionViewModelImpl)
}
