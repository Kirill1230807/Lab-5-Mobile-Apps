package com.example.lab5mobileapps.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable object OnBoardingScreenRoute

@Serializable object EnterNameScreenRoute

@Serializable object MainScreenRoute

@Serializable
object ListMainRoute

@Serializable
object GridMainRoute

@Serializable
object ProfileTabRoute

@Serializable
data class DetailsRoute(val placeId: Int)