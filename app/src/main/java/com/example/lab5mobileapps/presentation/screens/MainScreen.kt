package com.example.lab5mobileapps.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab5mobileapps.domain.repository.PlaceRepository
import com.example.lab5mobileapps.domain.repository.SettingsRepository
import com.example.lab5mobileapps.presentation.navigation.ListMainRoute
import com.example.lab5mobileapps.presentation.navigation.GridMainRoute
import com.example.lab5mobileapps.presentation.navigation.ProfileTabRoute
import com.example.lab5mobileapps.presentation.ui.theme.*

data class BottomNavItem<T : Any>(
    val route: T,
    val title: String,
    val icon: ImageVector
)

@Composable
fun MainScreen(
    userName: String,
    onNameChange: (String) -> Unit,
    placeRepository: PlaceRepository,
    settingsRepository: SettingsRepository
) {
    var currentTab by remember { mutableStateOf<Any>(ListMainRoute) }

    val bottomNavList = listOf(
        BottomNavItem(route = ListMainRoute, title = "Список", icon = Icons.Default.List),
        BottomNavItem(route = GridMainRoute, title = "Плитка", icon = Icons.Default.Star),
        BottomNavItem(route = ProfileTabRoute, title = "Профіль", icon = Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                bottomNavList.forEach { item ->
                    NavigationBarItem(
                        selected = currentTab == item.route,
                        onClick = { currentTab = item.route },
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = null)
                        },
                        label = { Text(text = item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (currentTab) {
                is ListMainRoute -> ListTabContent(placeRepository, settingsRepository)
                is GridMainRoute -> GridTabContent(placeRepository, settingsRepository)
                is ProfileTabRoute -> {
                    ProfileScreen(
                        userName = userName,
                        onNameChange = onNameChange
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, name = "Light Mode")
@Composable
private fun MainScreenPreview() {
    AppTheme {
        MainScreen(
            userName = "Кирило", onNameChange = {},
            placeRepository = TODO(),
            settingsRepository = TODO()
        )
    }
}

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun MainScreenPreviewDarkMode() {
    AppTheme {
        MainScreen(
            userName = "Кирило", onNameChange = {},
            placeRepository = TODO(),
            settingsRepository = TODO()
        )
    }
}