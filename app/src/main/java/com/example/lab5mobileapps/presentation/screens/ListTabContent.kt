package com.example.lab5mobileapps.presentation.screens

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.lab5mobileapps.data.repositoryImpl.PlaceRepositoryImpl
import com.example.lab5mobileapps.presentation.navigation.DetailsRoute
import com.example.lab5mobileapps.presentation.navigation.ListMainRoute
import com.example.lab5mobileapps.presentation.screenStates.PlaceScreenState
import com.example.lab5mobileapps.presentation.viewmodel.PlaceListViewModel
import com.example.lab5mobileapps.presentation.viewmodel.PlaceListViewModelFactory

@Composable
fun ListTabContent(
    viewModel: PlaceListViewModel = viewModel(
        factory = PlaceListViewModelFactory(
            PlaceRepositoryImpl()
        )
    )
) {
    val nestedNavController = rememberNavController()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(navController = nestedNavController, startDestination = ListMainRoute) {

        composable<ListMainRoute> {
            when (val state = uiState) {
                is PlaceScreenState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is PlaceScreenState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }

                is PlaceScreenState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Сортування: ${if (state.sortAscending) "А - Я" else "Я - А"}")
                            Switch(
                                checked = state.sortAscending,
                                onCheckedChange = { viewModel.setSortAscending(it) })
                        }

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.places) { place ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .clickable {
                                            nestedNavController.navigate(
                                                DetailsRoute(place.id)
                                            )
                                        }) {
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
                                            viewModel.toggleFavorite(place.id)
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
            }
        }

        composable<DetailsRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<DetailsRoute>()
            DetailScreen(
                placeId = args.placeId,
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

