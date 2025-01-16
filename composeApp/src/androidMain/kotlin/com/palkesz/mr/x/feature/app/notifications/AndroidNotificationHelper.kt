package com.palkesz.mr.x.feature.app.notifications

import android.app.NotificationManager
import com.palkesz.mr.x.core.util.di.getKoinInstance

actual fun setBadgeCount(badgeCount: Int) {
    // No need to do anything here as there are no app badges on Android
}

actual fun clearNotifications(ids: List<String>) {
    val notificationManager = getKoinInstance<NotificationManager>()
    ids.forEach {
        notificationManager.cancel(it.toInt())
    }
}
