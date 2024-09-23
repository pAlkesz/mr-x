package com.palkesz.mr.x

import androidx.compose.ui.window.ComposeUIViewController
import com.palkesz.mr.x.feature.app.appModule
import org.koin.core.context.startKoin

@Suppress("FunctionName", "Unused")
fun MainViewController() =
    ComposeUIViewController(configure = { startKoin { modules(appModule) } }) { App() }
