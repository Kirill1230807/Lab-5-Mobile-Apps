package com.example.lab5mobileapps.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lab5mobileapps.domain.model.Place
import com.example.lab5mobileapps.domain.repository.PlaceRepository
import com.example.lab5mobileapps.domain.repository.SettingsRepository
import com.example.lab5mobileapps.presentation.screenStates.PlaceScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlaceListViewModel(
    private val placeRepository: PlaceRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaceScreenState>(PlaceScreenState.Loading)
    val uiState: StateFlow<PlaceScreenState> = _uiState.asStateFlow()

    private var currentPlaces: List<Place> = emptyList()

    init {
        loadPlaces()
    }

    private fun loadPlaces() {
        viewModelScope.launch {
            try {
                combine(
                    placeRepository.getAllPlaces(),
                    settingsRepository.isSortAscending
                ) { places, isAscending ->
                    currentPlaces = places

                    val sortedList = if (isAscending) {
                        places.sortedBy { it.name }
                    } else {
                        places.sortedByDescending { it.name }
                    }

                    PlaceScreenState.Success(sortedList, isAscending)
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.value = PlaceScreenState.Error("Помилка: ${e.message}")
            }
        }
    }

    fun setSortAscending(ascending: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveSortAscending(ascending)
        }
    }

    fun toggleFavorite(placeId: Int) {
        val placeToUpdate = currentPlaces.find { it.id == placeId }
        placeToUpdate?.let { place ->
            viewModelScope.launch {
                val updatedPlace = place.copy(isFavourite = !place.isFavourite)
                placeRepository.updatePlace(updatedPlace)
            }
        }
    }
}

class PlaceListViewModelFactory(
    private val placeRepository: PlaceRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaceListViewModel(placeRepository, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}