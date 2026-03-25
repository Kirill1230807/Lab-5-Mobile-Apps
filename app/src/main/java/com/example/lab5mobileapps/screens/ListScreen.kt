package com.example.lab5mobileapps.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.lab5mobileapps.navigation.DetailsRoute
import com.example.lab5mobileapps.navigation.ListMainRoute

@Composable
fun ListTabContent() {
    val nestedNavController = rememberNavController()

    NavHost(navController = nestedNavController, startDestination = ListMainRoute) {

        composable<ListMainRoute> {
            var sortAscending by remember { mutableStateOf(true) }

            val sortedList by remember(sortAscending) {
                derivedStateOf {
                    if (sortAscending) mockPlaces.sortedBy { it.name }
                    else mockPlaces.sortedByDescending { it.name }
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
                    Text("Сортування: ${if (sortAscending) "А - Я" else "Я - А"}")
                    Switch(
                        checked = sortAscending,
                        onCheckedChange = { sortAscending = it }
                    )
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(sortedList) { place ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    nestedNavController.navigate(
                                        DetailsRoute(
                                            place.name,
                                            place.description
                                        )
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = place.name,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                IconButton(onClick = {
                                    val index = mockPlaces.indexOfFirst { it.id == place.id }
                                    if (index != -1) {
                                        mockPlaces[index] =
                                            mockPlaces[index].copy(isFavourite = !place.isFavourite)
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (place.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (place.isFavourite) Color.Red else Color.Gray
                                    )
                                }
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

@Preview(showSystemUi = true)
@Composable
private fun ListTabContentPreview() {
    ListTabContent()
}