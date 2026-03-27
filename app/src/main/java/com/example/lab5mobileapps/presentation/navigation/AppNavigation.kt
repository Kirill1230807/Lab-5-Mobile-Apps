package com.example.lab5mobileapps.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.lab5mobileapps.data.repositoryImpl.PlaceRepositoryImpl
import com.example.lab5mobileapps.presentation.screens.EnterNameScreen
import com.example.lab5mobileapps.presentation.screens.MainScreen
import com.example.lab5mobileapps.presentation.screens.OnBoardingScreenUI

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = OnBoardingScreenRoute,
    ) {
        composable<OnBoardingScreenRoute> { backStackEntry ->
            val savedName by backStackEntry
                .savedStateHandle
                .getStateFlow("user_name", "")
                .collectAsState()

            OnBoardingScreenUI(
                savedName = savedName,
                onNavigateToEnterName = { navController.navigate(EnterNameScreenRoute) },
                onNavigateToMain = { userName ->
                    navController.navigate(MainScreenRoute(userName = userName)) {
                        popUpTo<OnBoardingScreenRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<EnterNameScreenRoute> {
            EnterNameScreen(
                onSaveClick = { enteredName ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("user_name", enteredName)
                    navController.popBackStack()
                }
            )
        }

        composable<MainScreenRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<MainScreenRoute>()
            MainScreen(userName = route.userName)
        }
    }
}