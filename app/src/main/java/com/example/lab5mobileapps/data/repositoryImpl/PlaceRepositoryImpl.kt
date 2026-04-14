package com.example.lab5mobileapps.data.repositoryImpl

import com.example.lab5mobileapps.data.local.PlaceDao
import com.example.lab5mobileapps.domain.model.Place
import com.example.lab5mobileapps.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow

class PlaceRepositoryImpl(private val placeDao: PlaceDao) : PlaceRepository {


    override fun getAllPlaces(): Flow<List<Place>> {
        return placeDao.getAllPlaces()
    }

    override suspend fun getPlaceById(id: Int): Flow<Place> {
        return placeDao.getPlaceById(id)
    }

    override suspend fun updatePlace(place: Place) {
        placeDao.updatePlace(place)
    }
}