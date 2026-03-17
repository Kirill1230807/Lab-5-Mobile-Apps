package com.example.lab5mobileapps.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab5mobileapps.screens.EnterNameScreen
import com.example.lab5mobileapps.screens.OnBoardingScreenUI

data class BottomAppNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val bottomNavList = listOf(
        BottomAppNavItem(
            route = "home",
            title = "Головна",
            icon = Icons.Default.Home
        ),
        BottomAppNavItem(
            route = "favourites",
            title = "Обрані",
            icon = Icons.Default.Favorite
        ),
        BottomAppNavItem(
            route = "profile",
            title = "Профіль",
            icon = Icons.Default.Person
        ),
    )

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = OnBoardingScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<OnBoardingScreen> { backStackEntry ->
                val savedName by backStackEntry
                    .savedStateHandle
                    .getStateFlow("user_name", "")
                    .collectAsState()

                OnBoardingScreenUI(
                    savedName = savedName,
                    onNavigateToEnterName = { navController.navigate(EnterNameScreen) },
                    onNavigateToMain = { userName ->
                        navController.navigate(MainScreen(userName = userName)) {
                            popUpTo<OnBoardingScreen> { inclusive = true }
                        }
                    }
                )
            }

            composable<EnterNameScreen> {
                EnterNameScreen(
                    onSaveClick = { enteredName ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("user_name", enteredName)
                        navController.popBackStack()
                    }
                )
            }

            composable<MainScreen> {

            }
        }
    }
}