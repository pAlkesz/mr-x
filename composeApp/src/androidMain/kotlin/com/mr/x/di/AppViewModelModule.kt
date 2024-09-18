package com.mr.x.di

import com.mr.x.feature.app.AppViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val appViewModelModule = module {
	viewModelOf(::AppViewModel)
}
