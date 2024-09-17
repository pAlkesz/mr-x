package com.palkesz.mr.x.feature.home.loading

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val loadingViewModelModule = module {
	factoryOf(::LoadingViewModelImpl)
}
