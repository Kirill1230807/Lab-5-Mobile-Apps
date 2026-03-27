package com.example.lab5mobileapps.data.repositoryImpl

import com.example.lab5mobileapps.domain.model.Place
import com.example.lab5mobileapps.domain.repository.PlaceRepository

class PlaceRepositoryImpl : PlaceRepository {
    private val mockPlaces = listOf(
        Place(1, "Центральний парк", "Великий парк для прогулянок у центрі міста.", true),
        Place(2, "Національний музей", "Історичний музей з унікальними експонатами.", false),
        Place(3, "Стара фортеця", "Пам'ятка архітектури 16 століття.", true),
        Place(4, "Аквапарк", "Розважальний комплекс для всієї родини.", false),
        Place(5, "ТЦ Майдан", "Торговий центр у центрі міста.", false),
        Place(6, "ТЦ Формаркет", "Торговий центр із багатьма магазинами.", false),
        Place(7, "Калинівський ринок", "Ринок, де ти знайдеш все: від серветок до ракет.", true),
        Place(8, "Аквапарк", "Розважальний комплекс для всієї родини.", false),
        Place(9, "Аквапарк", "Розважальний комплекс для всієї родини.", false),
        Place(10, "Аквапарк", "Розважальний комплекс для всієї родини.", false),
        Place(11, "Ботанічний сад", "Величезна колекція екзотичних рослин.", true)
    )

    override suspend fun getPlaces(): List<Place> {
        return mockPlaces
    }

    override suspend fun getPlaceById(id: Int): Place? {
        return mockPlaces.find { it.id == id }
    }
}