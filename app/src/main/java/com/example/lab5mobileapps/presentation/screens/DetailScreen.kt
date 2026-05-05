package com.example.lab5mobileapps.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab5mobileapps.domain.repository.PlaceRepository
import com.example.lab5mobileapps.presentation.screenStates.PlaceDetailScreenState
import com.example.lab5mobileapps.presentation.viewmodel.PlaceDetailViewModel
import com.example.lab5mobileapps.presentation.viewmodel.PlaceDetailViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    placeId: String,
    onBackClick: () -> Unit,
    placeRepository: PlaceRepository
) {
    val factory = remember { PlaceDetailViewModelFactory(placeRepository) }
    val viewModel: PlaceDetailViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(placeId) {
        viewModel.loadPlaceDetails(placeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = if (uiState is PlaceDetailScreenState.Success) {
                        (uiState as PlaceDetailScreenState.Success).place.name
                    } else {
                        "Завантаження..."
                    }
                    Text(titleText)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (val state = uiState) {
                is PlaceDetailScreenState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is PlaceDetailScreenState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is PlaceDetailScreenState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Назва: ${state.place.name}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Опис: ${state.place.description}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Рейтинг: ${state.place.rating}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Категорія: ${state.place.category}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Ціна: ${state.place.price}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Контактний email: ${state.place.contactEmail}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Статистика та дані:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text = "Довжина опису: ${state.descriptionLength} символів")
                        Text(text = "Статус: ${state.statusText}")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    DetailScreen(
        placeId = "1",
        onBackClick = { },
        placeRepository = TODO()
    )
}