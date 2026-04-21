package com.example.lab5mobileapps.domain.repository

import com.example.lab5mobileapps.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getAllPlaces(): Flow<List<Place>>
    suspend fun refreshPlaces()
    suspend fun getPlaceById(id: String): Flow<Place>
    suspend fun updatePlace(place: Place)
    suspend fun createPlace(place: Place)
    suspend fun deletePlace(id: String)
}