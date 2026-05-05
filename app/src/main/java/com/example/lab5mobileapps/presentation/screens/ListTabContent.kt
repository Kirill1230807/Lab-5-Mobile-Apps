package com.example.lab5mobileapps.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    settingsRepository: SettingsRepository,
    windowSizeClass: WindowSizeClass
) {
    val nestedNavController = rememberNavController()

    val factory = remember { PlaceListViewModelFactory(placeRepository, settingsRepository) }
    val viewModel: PlaceListViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    var selectedPlaceId by rememberSaveable { mutableStateOf<String?>(null) }

    if (isExpanded) {
        // ДВОПАНЕЛЬНИЙ МАКЕТ ДЛЯ ПЛАНШЕТА
        Row(modifier = Modifier.fillMaxSize()) {
            // Ліва панель (Список)
            Box(modifier = Modifier.weight(1f)) {
                PlaceListSection(
                    uiState = uiState,
                    viewModel = viewModel,
                    onPlaceClick = { selectedPlaceId = it }
                )
            }

            // Права панель (Деталі)
            Box(modifier = Modifier.weight(1f)) {
                if (selectedPlaceId != null) {
                    DetailScreen(
                        placeId = selectedPlaceId!!,
                        onBackClick = { selectedPlaceId = null },
                        placeRepository = placeRepository
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Оберіть елемент зі списку для перегляду деталей",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    } else {

        NavHost(navController = nestedNavController, startDestination = ListMainRoute) {

            composable<ListMainRoute> {

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
                                        enableDismissFromStartToEnd = false,
                                        backgroundContent = {
                                            val color by animateColorAsState(
                                                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                                    Color.Red
                                                } else {
                                                    Color.Transparent
                                                }, label = "color"
                                            )

                                            Box(
                                                Modifier
                                                    .fillMaxSize()
                                                    .background(color)
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.CenterEnd
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Видалити",
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    ) {
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
}

@Composable
fun PlaceListSection(
    uiState: PlaceScreenState,
    viewModel: PlaceListViewModel,
    onPlaceClick: (String) -> Unit
) {
    when (val state = uiState) {
        is PlaceScreenState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                            .padding(8.dp), contentAlignment = Alignment.Center
                    ) {
                        Text("Немає підключення. Показані кешовані дані.", color = Color.White)
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
                                    viewModel.deletePlace(place.id); true
                                } else false
                            }
                        )
                        SwipeToDismissBox(
                            state = dismissState, enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color by animateColorAsState(targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Transparent)
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(16.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Видалити",
                                        tint = Color.White
                                    )
                                }
                            }
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable { onPlaceClick(place.id) }) {
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
                                    IconButton(onClick = { viewModel.toggleFavorite(place.id) }) {
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

@Preview(showSystemUi = true)
@Composable
private fun ListTabContentPreview() {
    ListTabContent(
        placeRepository = TODO(),
        settingsRepository = TODO(),
        windowSizeClass = TODO(),
    )
}

