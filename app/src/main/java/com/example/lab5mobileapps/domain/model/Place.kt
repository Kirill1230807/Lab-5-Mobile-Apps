package com.example.lab5mobileapps.domain.model

data class Place(
    val id: Int, val name: String,
    val description: String,
    val isFavourite: Boolean
)