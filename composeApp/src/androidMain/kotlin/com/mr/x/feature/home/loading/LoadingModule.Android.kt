package com.mr.x.feature.home.loading

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val loadingViewModelModule = module {
	viewModelOf(::LoadingViewModelImpl)
}
