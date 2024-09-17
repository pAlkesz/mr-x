package com.palkesz.mr.x.di

import com.palkesz.mr.x.feature.app.AppViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val appViewModelModule = module {
	factoryOf(::AppViewModel)
}
