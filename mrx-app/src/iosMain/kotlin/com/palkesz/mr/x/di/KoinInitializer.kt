package com.palkesz.mr.x.di

import org.koin.core.context.startKoin

actual class KoinInitializer {
	actual fun init() {
		startKoin {
			modules(appModule())
		}
	}
}
