package com.palkesz.mr.x.feature.app.notifications

import com.palkesz.mr.x.core.util.BUSINESS_TAG
import com.palkesz.mr.x.core.util.coroutines.CoroutineHelper
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSUserDefaults
import platform.UserNotifications.UNUserNotificationCenter

@Suppress("UNUSED")
class IOSNotificationHelper : KoinComponent {

    private val notificationHelper: NotificationHelper by inject()

    fun uploadToken(token: String?) = notificationHelper.uploadFcmToken(token = token)

    fun onNotificationClicked(data: Map<Any?, *>) =
        notificationHelper.onNotificationClicked(data = data)

    fun storeNotification(id: String, data: Map<Any?, *>) {
        CoroutineHelper.mainScope.launch {
            notificationHelper.storeNotification(id = id, data = data)?.let { badgeCount ->
                setBadgeCount(badgeCount = badgeCount)
            }
        }
    }

    fun shouldShowNotification(data: Map<Any?, *>) =
        notificationHelper.shouldShowNotification(data = data)

    fun clearNotificationsWithCurrentFilter() =
        notificationHelper.clearNotificationsWithCurrentFilter()
}

actual fun setBadgeCount(badgeCount: Int) {
    NSUserDefaults(suiteName = USER_DEFAULTS_SUIT_NAME).setInteger(
        value = badgeCount.toLong(),
        forKey = BADGE_COUNT_KEY,
    )
    UNUserNotificationCenter.currentNotificationCenter()
        .setBadgeCount(badgeCount.toLong()) { error ->
            error?.let {
                Napier.d(tag = BUSINESS_TAG) { "Error while updating badge count: $it" }
            } ?: Napier.d(tag = BUSINESS_TAG) { "Badge count updated: $badgeCount" }
        }
}

actual fun clearNotifications(ids: List<String>) {
    UNUserNotificationCenter.currentNotificationCenter()
        .removeDeliveredNotificationsWithIdentifiers(ids)
}

private const val BADGE_COUNT_KEY = "BadgeCount"
private const val USER_DEFAULTS_SUIT_NAME = "group.com.palkesz.mr.x.shared"
