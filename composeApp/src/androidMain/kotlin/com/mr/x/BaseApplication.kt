package com.mr.x

import android.app.Application
import com.mr.x.di.KoinInitializer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

class BaseApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		Firebase.initialize(this)
		KoinInitializer(applicationContext).init()
	}

}