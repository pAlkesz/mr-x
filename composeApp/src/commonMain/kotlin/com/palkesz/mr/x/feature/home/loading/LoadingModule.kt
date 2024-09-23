package com.palkesz.mr.x.feature.home.loading

import org.koin.core.module.Module
import org.koin.dsl.module

val loadingModule = module {
	includes(loadingViewModelModule)
}

expect val loadingViewModelModule: Module
