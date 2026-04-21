package com.example.lab5mobileapps.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lab5mobileapps.domain.model.Place
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places")
    fun getAllPlaces(): Flow<List<Place>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<Place>)

    @Update
    suspend fun updatePlace(place: Place)

    @Query("SELECT * FROM places WHERE id = :placeId")
    fun getPlaceById(placeId: String): Flow<Place>

    @Query("DELETE FROM places")
    suspend fun clearAll()

    @Query("DELETE FROM places WHERE id = :placeId")
    suspend fun deleteById(placeId: String)
}