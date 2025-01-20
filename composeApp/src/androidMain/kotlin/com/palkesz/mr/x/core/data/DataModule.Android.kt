package com.palkesz.mr.x.core.data

import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val dataStoreModule = module {
    single {
        createDataStore(producePath = {
            androidContext().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath.toPath()
        })
    }
}
