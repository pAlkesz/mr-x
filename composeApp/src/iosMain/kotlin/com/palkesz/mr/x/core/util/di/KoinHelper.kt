package com.palkesz.mr.x.core.util.di

import com.palkesz.mr.x.feature.app.appModule
import org.koin.core.context.startKoin

@Suppress("UNUSED")
fun initKoin() {
    startKoin { modules(appModule) }
}
