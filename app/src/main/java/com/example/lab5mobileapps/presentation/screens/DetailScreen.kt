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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab5mobileapps.data.repositoryImpl.PlaceRepositoryImpl
import com.example.lab5mobileapps.presentation.screenStates.PlaceDetailScreenState
import com.example.lab5mobileapps.presentation.viewmodel.PlaceDetailViewModel
import com.example.lab5mobileapps.presentation.viewmodel.PlaceDetailViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    placeId: Int,
    onBackClick: () -> Unit,
    viewModel: PlaceDetailViewModel = viewModel(
        factory = PlaceDetailViewModelFactory(placeId, PlaceRepositoryImpl())
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

                        Text(text = state.place.description, style = MaterialTheme.typography.bodyLarge)
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
        placeId = 1,
        onBackClick = {  },
        viewModel = viewModel()
    )
}