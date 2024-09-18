package com.mr.x.di

import com.mr.x.feature.app.AppViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val appViewModelModule = module {
	factoryOf(::AppViewModel)
}
