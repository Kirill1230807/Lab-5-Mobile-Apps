package com.example.lab5mobileapps.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.lab5mobileapps.navigation.DetailsRoute
import com.example.lab5mobileapps.navigation.GridMainRoute

@Composable
fun GridTabContent() {
    val nestedNavController = rememberNavController()

    NavHost(navController = nestedNavController, startDestination = GridMainRoute) {

        composable<GridMainRoute> {
            var showOnlyFavorites by remember { mutableStateOf(false) }

            val filteredList by remember(showOnlyFavorites) {
                derivedStateOf {
                    if (showOnlyFavorites) mockPlaces.filter { it.isFavourite }
                    else mockPlaces
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Тільки обрані")
                    Checkbox(
                        checked = showOnlyFavorites,
                        onCheckedChange = { showOnlyFavorites = it }
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(filteredList) { place ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .aspectRatio(1f)
                                .clickable {
                                    nestedNavController.navigate(
                                        DetailsRoute(
                                            place.name,
                                            place.description
                                        )
                                    )
                                }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = place.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        composable<DetailsRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<DetailsRoute>()
            DetailScreen(
                name = args.name,
                description = args.description,
                onBackClick = { nestedNavController.popBackStack() }
            )
        }
    }
}