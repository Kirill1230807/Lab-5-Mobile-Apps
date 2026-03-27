package com.example.lab5mobileapps.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lab5mobileapps.domain.model.Place
import com.example.lab5mobileapps.domain.repository.PlaceRepository
import com.example.lab5mobileapps.presentation.screenStates.PlaceScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaceListViewModel(private val placeRepository: PlaceRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<PlaceScreenState>(PlaceScreenState.Loading)
    val uiState: StateFlow<PlaceScreenState> = _uiState.asStateFlow()

    private var currentPlaces: List<Place> = emptyList()
    private var isSortAscending = true

    init {
        loadPlaces()
    }

    fun loadPlaces() {
        _uiState.value = PlaceScreenState.Loading
        viewModelScope.launch {
            delay(1000)
            try {
                currentPlaces = placeRepository.getPlaces()
                updateState()
            } catch (e: Exception) {
                _uiState.value = PlaceScreenState.Error("Помилка: ${e.message}")
            }
        }
    }


    fun setSortAscending(ascending: Boolean) {
        isSortAscending = ascending
        updateState()
    }

    fun toggleFavorite(placeId: Int) {
        currentPlaces = currentPlaces.map { place ->
            if (place.id == placeId) {
                place.copy(isFavourite = !place.isFavourite)
            } else {
                place
            }
        }
        updateState()
    }

    private fun updateState() {
        val sortedList = if (isSortAscending) {
            currentPlaces.sortedBy { it.name }
        } else {
            currentPlaces.sortedByDescending { it.name }
        }
        _uiState.value = PlaceScreenState.Success(sortedList, isSortAscending)
    }
}

class PlaceListViewModelFactory(private val repository: PlaceRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaceListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}