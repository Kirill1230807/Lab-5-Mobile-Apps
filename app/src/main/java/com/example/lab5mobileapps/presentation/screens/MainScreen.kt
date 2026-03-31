package com.example.lab5mobileapps.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab5mobileapps.presentation.navigation.ListMainRoute
import com.example.lab5mobileapps.presentation.navigation.GridMainRoute
import com.example.lab5mobileapps.presentation.navigation.ProfileTabRoute
import com.example.lab5mobileapps.presentation.ui.theme.*

data class BottomNavItem<T : Any>(
    val route: T,
    val title: String,
    val icon: ImageVector
)

//val mockPlaces = mutableStateListOf(
//    Place(1, "Центральний парк", "Великий парк для прогулянок у центрі міста.", true),
//    Place(2, "Національний музей", "Історичний музей з унікальними експонатами.", false),
//    Place(3, "Стара фортеця", "Пам'ятка архітектури 16 століття.", true),
//    Place(4, "Аквапарк", "Розважальний комплекс для всієї родини.", false),
//    Place(5, "ТЦ Майдан", "Торговий центр у центрі міста.", false),
//    Place(6, "ТЦ Формаркет", "Торговий центр із багатьма магазинами.", false),
//    Place(7, "Калинівський ринок", "Ринок, де ти знайдеш все: від серветок до ракет.", true),
//    Place(8, "Аквапарк", "Розважальний комплекс для всієї родини.", false),
//    Place(9, "Аквапарк", "Розважальний комплекс для всієї родини.", false),
//    Place(10, "Аквапарк", "Розважальний комплекс для всієї родини.", false),
//    Place(11, "Ботанічний сад", "Величезна колекція екзотичних рослин.", true)
//)

@Composable
fun MainScreen(userName: String) {
    var currentTab by remember { mutableStateOf<Any>(ListMainRoute) }

    var currentUserName by remember { mutableStateOf(userName) }

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
                is ListMainRoute -> ListTabContent()
                is GridMainRoute -> GridTabContent()
                is ProfileTabRoute -> {
                    ProfileScreen(
                        userName = currentUserName,
                        onNameChange = { newName -> currentUserName = newName }
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    MainScreen(userName = "Кирило")
}