package com.example.lab5mobileapps.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab5mobileapps.data.local.AppDatabase
import com.example.lab5mobileapps.data.remote.RetrofitClient
import com.example.lab5mobileapps.data.repositoryImpl.PlaceRepositoryImpl
import com.example.lab5mobileapps.data.repositoryImpl.SettingsRepositoryImpl
import com.example.lab5mobileapps.presentation.screens.EnterNameScreen
import com.example.lab5mobileapps.presentation.screens.MainScreen
import com.example.lab5mobileapps.presentation.screens.OnBoardingScreenUI
import com.example.lab5mobileapps.presentation.viewmodel.SettingsViewModel
import com.example.lab5mobileapps.presentation.viewmodel.SettingsViewModelFactory

@Composable
fun AppNavigation(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context, scope) }
    val apiService = RetrofitClient.apiService

    val placeRepository = remember { PlaceRepositoryImpl(
        database.placeDao(),
        apiService = apiService
    ) }
    val settingsRepository = remember { SettingsRepositoryImpl(context) }

    val factory = remember { SettingsViewModelFactory(settingsRepository) }
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

    val savedName by settingsViewModel.userName.collectAsState()

    if (savedName == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (savedName!!.isNotBlank()) MainScreenRoute else OnBoardingScreenRoute,
    ) {
        composable<OnBoardingScreenRoute> {
            OnBoardingScreenUI(
                savedName = savedName ?: "",
                onNavigateToEnterName = { navController.navigate(EnterNameScreenRoute) },
                onNavigateToMain = {
                    navController.navigate(MainScreenRoute) {
                        popUpTo<OnBoardingScreenRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<EnterNameScreenRoute> {
            EnterNameScreen(
                onSaveClick = { enteredName ->
                    settingsViewModel.saveUserName(enteredName)
                    navController.navigate(MainScreenRoute) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable<MainScreenRoute> {
            MainScreen(
                userName = savedName ?: "",
                onNameChange = { newName -> settingsViewModel.saveUserName(newName) },
                placeRepository = placeRepository,
                settingsRepository = settingsRepository,
                windowSizeClass = windowSizeClass
            )
        }
    }
}