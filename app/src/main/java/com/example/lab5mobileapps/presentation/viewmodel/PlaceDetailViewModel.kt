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
    private val placeId: Int,
    private val placeRepository: PlaceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<PlaceDetailScreenState>(PlaceDetailScreenState.Loading)
    val uiState: StateFlow<PlaceDetailScreenState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun loadDetails() {
        _uiState.value = PlaceDetailScreenState.Loading
        viewModelScope.launch {
            delay(500)
            try {
                val place = placeRepository.getPlaceById(placeId)
                if (place != null) {
                    val length = place.description.length
                    val status = if (place.isFavourite) "В улюблених" else "Звичайне місце"

                    _uiState.value = PlaceDetailScreenState.Success(
                        place = place,
                        descriptionLength = length,
                        statusText = status
                    )
                } else {
                    _uiState.value =
                        PlaceDetailScreenState.Error(message = "Місце з $placeId не знайдено")
                }
            } catch (e: Exception) {
                _uiState.value = PlaceDetailScreenState.Error("Помилка: ${e.message}")
            }
        }
    }
}

class PlaceDetailViewModelFactory(
    private val placeId: Int,
    private val repository: PlaceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaceDetailViewModel(placeId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}