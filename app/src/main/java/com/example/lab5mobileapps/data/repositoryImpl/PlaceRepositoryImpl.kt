package com.example.lab5mobileapps.data.repositoryImpl

import com.example.lab5mobileapps.data.local.PlaceDao
import com.example.lab5mobileapps.data.remote.PlaceApiService
import com.example.lab5mobileapps.domain.model.Place
import com.example.lab5mobileapps.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow

class PlaceRepositoryImpl(
    private val placeDao: PlaceDao,
    private val apiService: PlaceApiService
) : PlaceRepository {


    override fun getAllPlaces(): Flow<List<Place>> {
        return placeDao.getAllPlaces()
    }

    override suspend fun refreshPlaces() {
        val remotePlaces = apiService.getPlaces()
        placeDao.clearAll()
        placeDao.insertAll(remotePlaces)
    }

    override suspend fun getPlaceById(id: String): Flow<Place> {
        return placeDao.getPlaceById(id)
    }

    override suspend fun updatePlace(place: Place) {
        placeDao.updatePlace(place)
    }

    override suspend fun createPlace(place: Place) {
        val created = apiService.createPlace(place)
        placeDao.insertAll(listOf(created))
    }

    override suspend fun deletePlace(id: String) {
        placeDao.deleteById(id)
        try {
            apiService.deletePlace(id)
        } catch (e: Exception) {
            
        }
    }
}