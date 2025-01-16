package com.palkesz.mr.x.core.util.di

import android.app.NotificationManager
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    factory { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
}
