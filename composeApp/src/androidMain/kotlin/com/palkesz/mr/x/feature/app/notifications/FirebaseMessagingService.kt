package com.palkesz.mr.x.feature.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.palkesz.mr.x.R
import com.palkesz.mr.x.core.util.BUSINESS_TAG
import com.palkesz.mr.x.core.util.coroutines.CoroutineHelper
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MrxFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationHelper by inject<NotificationHelper>()

    private val notificationManager by inject<NotificationManager>()

    override fun onNewToken(token: String) {
        notificationHelper.uploadFcmToken(token = token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Napier.d(tag = BUSINESS_TAG) { "Remote notification received with data: ${message.data}" }
        message.notification?.takeIf {
            notificationHelper.shouldShowNotification(data = message.data.toMap())
        }?.let { notification ->
            val notificationId = System.currentTimeMillis().hashCode()
            notification.show(id = notificationId, intent = message.getPendingIntent())
            CoroutineHelper.mainScope.launch {
                notificationHelper.storeNotification(
                    id = notificationId.toString(),
                    data = message.data.toMap(),
                )
            }
        }
    }

    private fun RemoteMessage.Notification.show(id: Int, intent: PendingIntent) {
        createNotificationChannel()
        val builder =
            NotificationCompat.Builder(this@MrxFirebaseMessagingService, DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .build()
        notificationManager.notify(id, builder)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DEFAULT_CHANNEL_ID,
                DEFAULT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun RemoteMessage.getPendingIntent(): PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtras(this@getPendingIntent.data.toBundle())
        }
        return PendingIntent.getActivity(
            this@MrxFirebaseMessagingService,
            0,
            intent,
            FLAG_IMMUTABLE,
        )
    }

    private fun Map<String, String>.toBundle() = Bundle().also {
        forEach { (key, value) ->
            it.putString(key, value)
        }
    }

    companion object {
        private const val DEFAULT_CHANNEL_ID = "DEFAULT_CHANNEL_ID"
        private const val DEFAULT_CHANNEL_NAME = "Mr. X"
    }
}
