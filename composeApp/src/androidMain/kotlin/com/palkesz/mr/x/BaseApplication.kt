package com.palkesz.mr.x

import android.app.Application
import com.palkesz.mr.x.feature.app.appModule
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        Napier.base(DebugAntilog())
        startKoin {
            androidContext(applicationContext)
            modules(appModule)
        }
    }

}
