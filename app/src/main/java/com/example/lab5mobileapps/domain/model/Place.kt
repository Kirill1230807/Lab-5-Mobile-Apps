package com.example.lab5mobileapps.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val category: String,
    val isFavourite: Boolean = false,
    val rating: Double = 0.0,
    val imageRes: Int
)