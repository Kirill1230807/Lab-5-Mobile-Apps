package com.example.lab5mobileapps.presentation.screenStates

import com.example.lab5mobileapps.domain.model.Place

sealed interface PlaceScreenState {
    object Loading : PlaceScreenState
    data class Success(val places: List<Place>, val sortAscending: Boolean) : PlaceScreenState
    data class Error(val message: String) : PlaceScreenState
}