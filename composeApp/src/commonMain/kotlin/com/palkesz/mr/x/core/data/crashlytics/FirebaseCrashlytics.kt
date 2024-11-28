package com.palkesz.mr.x.core.data.crashlytics

import dev.gitlive.firebase.crashlytics.FirebaseCrashlytics

interface Crashlytics {
    fun setCrashlyticsCollectionEnabled(enabled: Boolean)
    fun setUserId(userId: String)

    interface Stub : Crashlytics {
        override fun setCrashlyticsCollectionEnabled(enabled: Boolean): Unit =
            throw NotImplementedError()

        override fun setUserId(userId: String): Unit = throw NotImplementedError()
    }
}

class CrashlyticsImpl(
    private val crashlytics: FirebaseCrashlytics
) : Crashlytics {

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        crashlytics.setCrashlyticsCollectionEnabled(enabled = enabled)
    }

    override fun setUserId(userId: String) {
        crashlytics.setUserId(userId = userId)
    }
}
