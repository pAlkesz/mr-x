package com.mr.x

import androidx.compose.ui.window.ComposeUIViewController
import com.mr.x.di.KoinInitializer

fun MainViewController() = ComposeUIViewController(
	configure = {
		KoinInitializer().init()
	}
) { App() }
