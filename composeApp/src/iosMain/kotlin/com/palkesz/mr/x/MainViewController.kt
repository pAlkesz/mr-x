package com.palkesz.mr.x

import androidx.compose.ui.window.ComposeUIViewController
import com.palkesz.mr.x.di.KoinInitializer

fun MainViewController() = ComposeUIViewController(
	configure = {
		KoinInitializer().init()
	}
) { App() }
