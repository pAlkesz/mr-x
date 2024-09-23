package com.palkesz.mr.x.di

import com.palkesz.mr.x.feature.app.AppViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val appViewModelModule = module {
	viewModelOf(::AppViewModel)
}
