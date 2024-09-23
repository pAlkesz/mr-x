package com.palkesz.mr.x.feature.app

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

actual val appViewModelModule = module {
    viewModelOf(::AppViewModel)
}
