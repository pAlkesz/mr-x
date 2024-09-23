package com.palkesz.mr.x.feature.app

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val appViewModelModule = module {
    factoryOf(::AppViewModel)
}
