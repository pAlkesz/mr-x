package com.palkesz.mr.x

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import com.palkesz.mr.x.core.util.di.androidModule
import com.palkesz.mr.x.core.util.platform.isDebug
import com.palkesz.mr.x.feature.app.appModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            if (isDebug) DebugAppCheckProviderFactory.getInstance() else PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
        Napier.base(antilog = DebugAntilog())
        startKoin {
            androidContext(applicationContext)
            modules(appModule, androidModule)
        }
    }
}
