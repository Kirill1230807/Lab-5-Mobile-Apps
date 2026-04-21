package com.example.lab5mobileapps.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lab5mobileapps.domain.repository.PlaceRepository
import com.example.lab5mobileapps.presentation.screenStates.PlaceDetailScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaceDetailViewModel(
    private val placeRepository: PlaceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaceDetailScreenState>(PlaceDetailScreenState.Loading)
    val uiState: StateFlow<PlaceDetailScreenState> = _uiState.asStateFlow()

    fun loadPlaceDetails(id: String) {
        _uiState.value = PlaceDetailScreenState.Loading

        viewModelScope.launch {
            try {
                placeRepository.getPlaceById(id).collect { place ->
                    if (place != null) {
                        val descLength = place.description.length
                        val status = if (place.isFavourite) "В обраному" else "Не в обраному"

                        _uiState.value = PlaceDetailScreenState.Success(
                            place = place,
                            descriptionLength = descLength,
                            statusText = status
                        )
                    } else {
                        _uiState.value = PlaceDetailScreenState.Error("Місце не знайдено")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = PlaceDetailScreenState.Error("Помилка: ${e.message}")
            }
        }
    }

    fun toggleFavorite() {
        val currentState = _uiState.value
        if (currentState is PlaceDetailScreenState.Success) {
            val updatedPlace =
                currentState.place.copy(isFavourite = !currentState.place.isFavourite)

            viewModelScope.launch {
                placeRepository.updatePlace(updatedPlace)
            }
        }
    }
}

class PlaceDetailViewModelFactory(private val repository: PlaceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaceDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}