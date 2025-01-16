package com.palkesz.mr.x.feature.app.notifications

import com.palkesz.mr.x.core.data.datastore.MrxDataStore
import com.palkesz.mr.x.core.data.messaging.MessagingRepository
import com.palkesz.mr.x.core.util.BUSINESS_TAG
import com.palkesz.mr.x.core.util.coroutines.CoroutineHelper
import com.palkesz.mr.x.core.util.extensions.safeLet
import com.palkesz.mr.x.feature.app.AppEvent
import com.palkesz.mr.x.proto.LocalNotificationType
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

interface NotificationHelper {
    val event: SharedFlow<AppEvent.NavigateToGame?>

    fun uploadFcmToken(token: String?)
    fun shouldShowNotification(data: Map<Any?, *>): Boolean
    suspend fun storeNotification(id: String, data: Map<Any?, *>): Int?
    fun onNotificationClicked(data: Map<Any?, *>)
    fun setNotificationFilter(filter: NotificationFilter)
    fun clearNotificationsWithCurrentFilter()

    interface Stub : NotificationHelper {
        override val event: SharedFlow<AppEvent.NavigateToGame?>
            get() = throw NotImplementedError()

        override fun uploadFcmToken(token: String?) = throw NotImplementedError()
        override fun shouldShowNotification(data: Map<Any?, *>) = throw NotImplementedError()
        override suspend fun storeNotification(id: String, data: Map<Any?, *>) =
            throw NotImplementedError()

        override fun onNotificationClicked(data: Map<Any?, *>) = throw NotImplementedError()
        override fun setNotificationFilter(filter: NotificationFilter) = throw NotImplementedError()
        override fun clearNotificationsWithCurrentFilter() = throw NotImplementedError()
    }
}

class NotificationHelperImpl(
    private val messagingRepository: MessagingRepository,
    private val mrxDataStore: MrxDataStore,
) : NotificationHelper {

    private val _event = MutableSharedFlow<AppEvent.NavigateToGame?>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val event = _event.asSharedFlow()

    private var notificationFilter =
        NotificationFilter(gameId = null, type = LocalNotificationType.QUESTION)

    override fun onNotificationClicked(data: Map<Any?, *>) {
        val payloadData = data.mapValues { it.value.toString() }
        safeLet(payloadData[GAME_ID_KEY], payloadData[TYPE_KEY]) { gameId, notificationType ->
            Napier.d(tag = BUSINESS_TAG) { "Notification clicked with payloadData: $data" }
            val type = LocalNotificationType.valueOf(value = notificationType)
            _event.tryEmit(AppEvent.NavigateToGame(gameId = gameId, type = type))
        }
    }

    override fun uploadFcmToken(token: String?) {
        Napier.d(tag = BUSINESS_TAG) { "Uploading token: $token" }
        token?.let {
            CoroutineHelper.mainScope.launch {
                messagingRepository.uploadToken(token = it)
            }
        }
    }

    override fun shouldShowNotification(data: Map<Any?, *>) =
        data.asPayloadData().showNotification()

    override suspend fun storeNotification(id: String, data: Map<Any?, *>): Int? {
        val payloadData = data.asPayloadData()
        val type = payloadData[TYPE_KEY].orEmpty()
        return mrxDataStore.storeNotification(
            id = id,
            gameId = payloadData[GAME_ID_KEY].orEmpty(),
            type = LocalNotificationType.valueOf(value = type),
        ).getOrNull()
    }

    override fun setNotificationFilter(filter: NotificationFilter) {
        notificationFilter = filter
        clearNotificationsWithCurrentFilter()
    }

    override fun clearNotificationsWithCurrentFilter() {
        notificationFilter.gameId?.let { gameId ->
            CoroutineHelper.mainScope.launch {
                mrxDataStore.clearNotifications(gameId = gameId, type = notificationFilter.type)
                    .onSuccess { (clearedNotifications, badgeCount) ->
                        clearNotifications(ids = clearedNotifications.map { it.id })
                        setBadgeCount(badgeCount = badgeCount)
                    }
            }
        }
    }

    private fun Map<String, String>.showNotification() =
        (this[GAME_ID_KEY] != notificationFilter.gameId ||
                this[TYPE_KEY] != notificationFilter.type.name)

    private fun Map<Any?, *>.asPayloadData() = keys.filterNotNull().filterIsInstance<String>()
        .associateWith { key -> this[key].toString() }

    companion object {
        private const val GAME_ID_KEY = "gameId"
        private const val TYPE_KEY = "type"
    }
}

expect fun setBadgeCount(badgeCount: Int)

expect fun clearNotifications(ids: List<String>)
