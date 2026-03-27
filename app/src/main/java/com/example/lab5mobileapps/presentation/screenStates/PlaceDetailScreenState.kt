package com.example.lab5mobileapps.presentation.screenStates

import com.example.lab5mobileapps.domain.model.Place

sealed interface PlaceDetailScreenState {
    object Loading: PlaceDetailScreenState
    data class Success(
        val place: Place,
        val descriptionLength: Int,
        val statusText: String
        ) : PlaceDetailScreenState
    data class Error(val message: String) : PlaceDetailScreenState
}