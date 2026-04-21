package com.example.lab5mobileapps.data.remote

import com.example.lab5mobileapps.domain.model.Place
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PlaceApiService {
    @GET("places")
    suspend fun getPlaces(): List<Place>

    @GET("places/{id}")
    suspend fun getPlaceById(@Path("id") id: String): Place

    @POST("places")
    suspend fun createPlace(@Body place: Place): Place

    @DELETE("places/{id}")
    suspend fun deletePlace(@Path("id") id: String)
}