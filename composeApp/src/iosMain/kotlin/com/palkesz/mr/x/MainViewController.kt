package com.palkesz.mr.x

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import com.palkesz.mr.x.core.util.platform.isDebug
import com.palkesz.mr.x.feature.app.appModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

@Suppress("FunctionName", "Unused")
fun MainViewController() = ComposeUIViewController(configure = {
    Napier.base(DebugAntilog())
    onFocusBehavior = OnFocusBehavior.DoNothing
    startKoin { modules(appModule) }
    if (!isDebug) {
        setCrashlyticsUnhandledExceptionHook()
    }
}) { App() }
