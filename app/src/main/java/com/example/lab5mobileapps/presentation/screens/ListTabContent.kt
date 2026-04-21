package com.example.lab5mobileapps.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.lab5mobileapps.domain.repository.PlaceRepository
import com.example.lab5mobileapps.domain.repository.SettingsRepository
import com.example.lab5mobileapps.presentation.components.AddPlaceDialog
import com.example.lab5mobileapps.presentation.navigation.DetailsRoute
import com.example.lab5mobileapps.presentation.navigation.ListMainRoute
import com.example.lab5mobileapps.presentation.screenStates.PlaceScreenState
import com.example.lab5mobileapps.presentation.viewmodel.PlaceListViewModel
import com.example.lab5mobileapps.presentation.viewmodel.PlaceListViewModelFactory

@Composable
fun ListTabContent(
    placeRepository: PlaceRepository,
    settingsRepository: SettingsRepository
) {
    val nestedNavController = rememberNavController()

    val factory = remember { PlaceListViewModelFactory(placeRepository, settingsRepository) }
    val viewModel: PlaceListViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }

    NavHost(navController = nestedNavController, startDestination = ListMainRoute) {

        composable<ListMainRoute> {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    when (val state = uiState) {
                        is PlaceScreenState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is PlaceScreenState.Error -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = state.message, color = MaterialTheme.colorScheme.error)
                                Button(onClick = { viewModel.fetchFromNetwork() }) { Text("Повторити") }
                            }
                        }

                        is PlaceScreenState.Success -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                if (state.isOffline) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.Red.copy(alpha = 0.6f))
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Немає підключення. Показані кешовані дані.",
                                            color = Color.White
                                        )
                                    }
                                }
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
                                    items(state.places, key = { it.id }) { place ->
                                        val dismissState = rememberSwipeToDismissBoxState(
                                            confirmValueChange = {
                                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                                    viewModel.deletePlace(place.id)
                                                    true
                                                } else false
                                            }
                                        )

                                        SwipeToDismissBox(
                                            state = dismissState,
                                            backgroundContent = {
                                                Box(
                                                    Modifier
                                                        .fillMaxSize()
                                                        .background(Color.Red)
                                                        .padding(16.dp),
                                                    contentAlignment = Alignment.CenterEnd
                                                ) {
                                                    Text("Видалити", color = Color.White)
                                                }
                                            }
                                        ) { }
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
            }

            if (showAddDialog) {
                AddPlaceDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { newPlace ->
                        viewModel.createPlace(newPlace)
                        showAddDialog = false
                    }
                )
            }
        }

        composable<DetailsRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<DetailsRoute>()
            DetailScreen(
                placeId = args.placeId,
                onBackClick = { nestedNavController.popBackStack() },
                placeRepository = placeRepository
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ListTabContentPreview() {
    ListTabContent(
        placeRepository = TODO(),
        settingsRepository = TODO()
    )
}

