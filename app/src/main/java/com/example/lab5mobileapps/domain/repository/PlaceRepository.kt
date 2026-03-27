package com.example.lab5mobileapps.domain.repository

import com.example.lab5mobileapps.domain.model.Place

interface PlaceRepository {
    suspend fun getPlaces(): List<Place>
    suspend fun getPlaceById(id: Int): Place?
}