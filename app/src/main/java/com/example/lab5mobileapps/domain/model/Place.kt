package com.example.lab5mobileapps.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Entity(tableName = "places")
@Serializable
data class Place(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val category: String,
    val isFavourite: Boolean = false,
    val rating: Double = 0.0,
    val price: Double = 0.0,
    val contactEmail: String = "",
    val imageRes: Int = 0
)