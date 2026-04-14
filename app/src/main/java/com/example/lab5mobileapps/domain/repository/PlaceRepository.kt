package com.example.lab5mobileapps.domain.repository

import com.example.lab5mobileapps.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getAllPlaces(): Flow<List<Place>>
    suspend fun getPlaceById(id: Int): Flow<Place>
    suspend fun updatePlace(place: Place)
}