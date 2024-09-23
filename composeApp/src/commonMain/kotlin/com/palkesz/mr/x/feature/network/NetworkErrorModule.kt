package com.palkesz.mr.x.feature.network

import org.koin.dsl.module

val networkErrorModule = module {
    single { NetworkErrorRepository() }
}
