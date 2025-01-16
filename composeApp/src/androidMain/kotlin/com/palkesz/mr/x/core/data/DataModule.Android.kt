package com.palkesz.mr.x.core.data

import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val dataStoreModule = module {
    single {
        createPreferencesDataStore(producePath = {
            androidContext().filesDir.resolve(PREFERENCES_DATA_STORE_FILE_NAME).absolutePath
        })
    }
    single {
        createNotificationsDataStore(producePath = {
            androidContext().filesDir.resolve(NOTIFICATIONS_DATA_STORE_FILE_NAME).absolutePath.toPath()
        })
    }
}
