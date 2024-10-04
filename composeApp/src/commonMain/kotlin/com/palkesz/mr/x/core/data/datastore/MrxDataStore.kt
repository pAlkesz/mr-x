package com.palkesz.mr.x.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

interface MrxDataStore {
    suspend fun getUserEmail(): String?
    suspend fun storeUserEmail(email: String): Result<Unit>
}

class MrXDataStoreImpl(
    private val dataStore: DataStore<Preferences>,
) : MrxDataStore {

    override suspend fun getUserEmail(): String? = dataStore.data.first()[USER_EMAIL_KEY]

    override suspend fun storeUserEmail(email: String) = try {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
        Result.success(Unit)
    } catch (exception: Exception) {
        Result.failure(exception = exception)
    }

    companion object {
        private val USER_EMAIL_KEY = stringPreferencesKey("USER_EMAIL_KEY")
    }
}
