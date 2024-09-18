package com.mr.x.feature.games.question.barkochba

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val barkochbaQuestionViewModelModule = module {
	factoryOf(::BarkochbaQuestionViewModelImpl)
}
