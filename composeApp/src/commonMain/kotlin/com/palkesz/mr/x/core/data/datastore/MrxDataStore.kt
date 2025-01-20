package com.palkesz.mr.x.core.data.datastore

import androidx.datastore.core.DataStore
import com.palkesz.mr.x.proto.LocalNotification
import com.palkesz.mr.x.proto.LocalNotificationType
import com.palkesz.mr.x.proto.LocalNotifications
import com.palkesz.mr.x.proto.MrXData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface MrxDataStore {
    suspend fun getUserEmail(): String?
    suspend fun storeUserEmail(email: String): Result<Unit>
    fun observeNotificationCount(gameIds: List<String>): Flow<List<Pair<String, Int>>>
    fun observeNotificationCount(gameId: String): Flow<Pair<Int, Int>>
    suspend fun storeNotification(
        id: String,
        gameId: String,
        type: LocalNotificationType
    ): Result<Int>

    suspend fun clearNotifications(
        gameId: String,
        type: LocalNotificationType
    ): Result<Pair<List<LocalNotification>, Int>>

    interface Stub : MrxDataStore {
        override suspend fun getUserEmail(): String? = throw NotImplementedError()
        override suspend fun storeUserEmail(email: String): Result<Unit> =
            throw NotImplementedError()

        override fun observeNotificationCount(gameIds: List<String>): Flow<List<Pair<String, Int>>> =
            throw NotImplementedError()

        override fun observeNotificationCount(gameId: String): Flow<Pair<Int, Int>> =
            throw NotImplementedError()

        override suspend fun storeNotification(
            id: String,
            gameId: String,
            type: LocalNotificationType
        ): Result<Int> = throw NotImplementedError()

        override suspend fun clearNotifications(
            gameId: String,
            type: LocalNotificationType
        ): Result<Pair<List<LocalNotification>, Int>> = throw NotImplementedError()
    }
}

class MrXDataStoreImpl(
    private val dataStore: DataStore<MrXData>,
) : MrxDataStore {

    override suspend fun getUserEmail(): String = dataStore.data.first().email

    override suspend fun storeUserEmail(email: String) = runCatching {
        dataStore.updateData { data ->
            data.copy(email = email)
        }
        Unit
    }

    override fun observeNotificationCount(gameIds: List<String>) = dataStore.data.map { data ->
        gameIds.map { id ->
            id to (data.notifications[id]?.notifications?.size ?: 0)
        }
    }

    override fun observeNotificationCount(gameId: String) = dataStore.data.map { data ->
        val notifications = data.notifications[gameId]?.notifications.orEmpty()
        Pair(
            first = notifications.count { it.type == LocalNotificationType.QUESTION },
            second = notifications.count { it.type == LocalNotificationType.BARKOCHBA },
        )
    }

    override suspend fun storeNotification(
        id: String,
        gameId: String,
        type: LocalNotificationType
    ) = runCatching {
        dataStore.updateData { data ->
            val notifications = data.notifications.toMutableMap()
            notifications[gameId] = LocalNotifications(
                notifications = (notifications[gameId]?.notifications.orEmpty() + LocalNotification(
                    id = id,
                    type = type
                )).distinctBy { it.id }
            )
            data.copy(notifications = notifications)
        }
        dataStore.data.first().notifications.values.sumOf {
            it.notifications.size
        }
    }

    override suspend fun clearNotifications(gameId: String, type: LocalNotificationType) =
        runCatching {
            var deletedNotifications: List<LocalNotification> = emptyList()
            var badgeCount = 0
            dataStore.updateData { data ->
                val notifications = data.notifications.toMutableMap()
                val (clearedNotifications, keptNotifications) =
                    notifications[gameId]?.notifications.orEmpty().partition { it.type == type }
                deletedNotifications = clearedNotifications
                notifications[gameId] = LocalNotifications(notifications = keptNotifications)
                badgeCount = notifications.values.sumOf {
                    it.notifications.size
                }
                data.copy(notifications = notifications)
            }
            deletedNotifications to badgeCount
        }
}
