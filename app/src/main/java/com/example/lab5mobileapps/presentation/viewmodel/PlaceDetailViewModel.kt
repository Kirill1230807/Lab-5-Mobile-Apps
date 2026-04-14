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

class PlaceDetailViewModel(private val placeRepository: PlaceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaceDetailScreenState>(PlaceDetailScreenState.Loading)
    val uiState: StateFlow<PlaceDetailScreenState> = _uiState.asStateFlow()

    fun loadPlaceDetails(id: Int) {
        _uiState.value = PlaceDetailScreenState.Loading

        viewModelScope.launch {
            try {
                // Підписуємось на конкретне місце з бази
                placeRepository.getPlaceById(id).collect { place ->
                    // Перевіряємо, чи повернула база об'єкт (на випадок, якщо його ще немає)
                    if (place != null) {
                        // Вираховуємо необхідні для UI параметри
                        val descLength = place.description.length
                        val status = if (place.isFavourite) "В обраному" else "Не в обраному"

                        // Передаємо в UI готовий стан
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

    // Якщо на екрані деталей теж є кнопка "сердечко"
    fun toggleFavorite() {
        val currentState = _uiState.value
        // Перевіряємо, чи ми зараз у стані Success і чи маємо об'єкт
        if (currentState is PlaceDetailScreenState.Success) {
            val updatedPlace =
                currentState.place.copy(isFavourite = !currentState.place.isFavourite)

            viewModelScope.launch {
                // Відправляємо оновлення в базу.
                // Завдяки .collect() у функції loadPlaceDetails, екран оновиться автоматично!
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