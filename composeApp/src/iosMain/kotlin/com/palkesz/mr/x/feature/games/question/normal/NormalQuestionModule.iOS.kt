package com.palkesz.mr.x.feature.games.question.normal

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val normalQuestionViewModelModule = module {
	factoryOf(::NormalQuestionViewModelImpl)
}
