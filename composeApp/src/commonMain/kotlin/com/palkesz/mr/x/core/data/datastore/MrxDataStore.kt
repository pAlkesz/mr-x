package com.palkesz.mr.x.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.palkesz.mr.x.proto.LocalNotification
import com.palkesz.mr.x.proto.LocalNotificationMap
import com.palkesz.mr.x.proto.LocalNotificationType
import com.palkesz.mr.x.proto.LocalNotifications
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
    private val preferencesDataStore: DataStore<Preferences>,
    private val notificationsDataStore: DataStore<LocalNotificationMap>,
) : MrxDataStore {

    override suspend fun getUserEmail(): String? = preferencesDataStore.data.first()[USER_EMAIL_KEY]

    override suspend fun storeUserEmail(email: String) = try {
        preferencesDataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
        Result.success(Unit)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    override fun observeNotificationCount(gameIds: List<String>) =
        notificationsDataStore.data.map { data ->
            gameIds.map { id ->
                id to (data.notifications[id]?.notifications?.size ?: 0)
            }
        }

    override fun observeNotificationCount(gameId: String) =
        notificationsDataStore.data.map { data ->
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
    ) = try {
        notificationsDataStore.updateData { data ->
            val notifications = data.notifications.toMutableMap()
            notifications[gameId] = LocalNotifications(
                notifications = (notifications[gameId]?.notifications.orEmpty() + LocalNotification(
                    id = id,
                    type = type
                )).distinctBy { it.id }
            )
            LocalNotificationMap(notifications = notifications)
        }
        val badgeCount = notificationsDataStore.data.first().notifications.values.sumOf {
            it.notifications.size
        }
        Result.success(value = badgeCount)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    override suspend fun clearNotifications(gameId: String, type: LocalNotificationType) = try {
        var deletedNotifications: List<LocalNotification> = emptyList()
        var badgeCount = 0
        notificationsDataStore.updateData { data ->
            val notifications = data.notifications.toMutableMap()
            val (clearedNotifications, keptNotifications) =
                notifications[gameId]?.notifications.orEmpty().partition { it.type == type }
            deletedNotifications = clearedNotifications
            notifications[gameId] = LocalNotifications(notifications = keptNotifications)
            badgeCount = notifications.values.sumOf {
                it.notifications.size
            }
            LocalNotificationMap(notifications = notifications)
        }
        Result.success(value = deletedNotifications to badgeCount)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    companion object {
        private val USER_EMAIL_KEY = stringPreferencesKey("USER_EMAIL_KEY")
    }
}
