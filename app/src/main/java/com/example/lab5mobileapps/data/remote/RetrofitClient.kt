package com.example.lab5mobileapps.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {
    private const val BASE_URL = "https://69e7ce6e68208c1debe95ad4.mockapi.io/api/lab9/"

    private val json = Json { ignoreUnknownKeys = true }

    val apiService : PlaceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PlaceApiService::class.java)
    }
}