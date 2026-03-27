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

class PlaceGridViewModel(private val placeRepository: PlaceRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<PlaceScreenState>(PlaceScreenState.Loading)
    val uiState: StateFlow<PlaceScreenState> = _uiState.asStateFlow()

    private var currentPlaces: List<Place> = emptyList()
    private var isFavouriteOn = false


    init {
        loadPlaces()
    }

    private fun loadPlaces() {
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

    fun setFavoriteFilter(isChecked: Boolean) {
        isFavouriteOn = isChecked
        updateState()
    }

    private fun updateState() {
        val sortedList = if (isFavouriteOn) {
            currentPlaces.filter { it.isFavourite }
        } else {
            currentPlaces.sortedByDescending { it.name }
        }
        _uiState.value = PlaceScreenState.Success(sortedList, isFavouriteOn)
    }
}

class PlaceGridViewModelFactory(private val repository: PlaceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceGridViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaceGridViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}