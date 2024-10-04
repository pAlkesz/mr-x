package com.palkesz.mr.x.core.data

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val dataStoreModule = module {
    single {
        createDataStore(producePath = {
            androidContext().filesDir.resolve(dataStoreFileName).absolutePath
        })
    }
}
