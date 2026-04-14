package com.example.lab5mobileapps.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userName: Flow<String>
    suspend fun saveUserName(name: String)
    val isSortAscending: Flow<Boolean>
    suspend fun saveSortAscending(isAscending: Boolean)
}