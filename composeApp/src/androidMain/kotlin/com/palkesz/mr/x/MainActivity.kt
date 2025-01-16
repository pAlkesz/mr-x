package com.palkesz.mr.x

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.palkesz.mr.x.core.util.BUSINESS_TAG
import com.palkesz.mr.x.feature.app.notifications.NotificationHelper
import dev.theolm.rinku.compose.ext.Rinku
import io.github.aakira.napier.Napier
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val notificationHelper by inject<NotificationHelper>()

    private val requestPermissionLauncher = registerForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Napier.d(tag = BUSINESS_TAG) { "Notification permission granted: $isGranted" }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        askNotificationPermission()
        uploadFirebaseToken()
        handleIntent(intent = intent)
        setContent {
            Rinku {
                App()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent = intent)
    }

    private fun askNotificationPermission() {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                // Nothing to do here as no notification permission needed before Android 13
            }

            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Notification permission already granted
            }

            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun uploadFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Napier.d(tag = BUSINESS_TAG) { "Fetching FCM registration token failed ${task.exception}" }
                return@OnCompleteListener
            }
            notificationHelper.uploadFcmToken(token = task.result)
        })
    }

    private fun handleIntent(intent: Intent) {
        intent.extras?.toMap()?.let {
            notificationHelper.onNotificationClicked(data = it)
        }
    }

    private fun Bundle.toMap(): Map<Any?, *> = keySet().mapNotNull { key ->
        getString(key)?.let { key to it }
    }.toMap()
}
