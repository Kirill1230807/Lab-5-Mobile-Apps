package com.example.lab5mobileapps.navigation

import kotlinx.serialization.Serializable

@Serializable object OnBoardingScreenRoute

@Serializable object EnterNameScreenRoute

@Serializable data class MainScreenRoute(val userName: String)

@Serializable
object ListMainRoute

@Serializable
object GridMainRoute

@Serializable
object ProfileTabRoute

@Serializable
data class DetailsRoute(val name: String, val description: String)