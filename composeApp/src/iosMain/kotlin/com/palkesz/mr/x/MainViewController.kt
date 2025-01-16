package com.palkesz.mr.x

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import com.palkesz.mr.x.core.util.platform.isDebug

@Suppress("FunctionName", "Unused")
fun MainViewController() = ComposeUIViewController(configure = {
    onFocusBehavior = OnFocusBehavior.DoNothing
    if (!isDebug) {
        setCrashlyticsUnhandledExceptionHook()
    }
}) { App() }
