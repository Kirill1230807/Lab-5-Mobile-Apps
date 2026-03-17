package com.example.lab5mobileapps.navigation

import kotlinx.serialization.Serializable

@Serializable object OnBoardingScreen

@Serializable object EnterNameScreen

@Serializable data class MainScreen(val userName: String)