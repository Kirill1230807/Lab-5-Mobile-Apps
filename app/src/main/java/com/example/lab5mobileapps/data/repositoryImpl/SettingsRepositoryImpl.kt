package com.example.lab5mobileapps.data.repositoryImpl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.lab5mobileapps.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl(val context: Context) : SettingsRepository {

    private val USER_NAME_KEY = stringPreferencesKey("user_name")

    private val SORT_ASCENDING_KEY = booleanPreferencesKey("sort_ascending")

    override val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY] ?: ""
    }

    override suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    override val isSortAscending: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SORT_ASCENDING_KEY] ?: true
    }

    override suspend fun saveSortAscending(isAscending: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SORT_ASCENDING_KEY] = isAscending
        }
    }
}