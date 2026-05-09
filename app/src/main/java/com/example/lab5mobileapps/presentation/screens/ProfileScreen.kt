package com.example.lab5mobileapps.presentation.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class LocationState {
    object Idle : LocationState()
    object Loading : LocationState()
    data class Success(
        val latitude: Double,
        val longitude: Double,
        val accuracy: Float,
        val time: Long,
        val distance: Float
    ) : LocationState()

    data class Error(val message: String) : LocationState()
}

@Composable
fun ProfileScreen(
    userName: String,
    onNameChange: (String) -> Unit
) {

    val context = LocalContext.current
    val activity = context as? Activity

    val avatarFile = File(context.filesDir, "profile_avatar.jpg")

    var avatarImageKey by remember { mutableStateOf(avatarFile.lastModified().toString()) }

    var hasAvatar by remember { mutableStateOf(avatarFile.exists()) }

    var permissionState by remember {
        mutableStateOf(
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> "GRANTED"

                activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.CAMERA
                    )
                } == true -> "RATIONALE"

                else -> "DENIED_OR_NOT_ASKED"
            }
        )
    }

    // Лаунчер для запиту системного дозволу
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState = when {
            isGranted -> "GRANTED"
            activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    Manifest.permission.CAMERA
                )
            } == true -> "RATIONALE"

            else -> "PERMANENTLY_DENIED" // Якщо відхилено і rationale == false, значить вибрано "Не питати знову"
        }
    }

    // Лаунчер для камери, який зберігає фото за переданим URI
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            hasAvatar = true
            // Оновлюємо ключ, щоб Coil перезавантажив нове фото замість старого кешованого
            avatarImageKey = System.currentTimeMillis().toString()
        }
    }

    var locationState by remember { mutableStateOf<LocationState>(LocationState.Idle) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Константи
    val targetLatitude = 50.4501
    val targetLongitude = 30.5234

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        locationState = LocationState.Loading
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        location.latitude, location.longitude,
                        targetLatitude, targetLongitude,
                        results
                    )
                    locationState = LocationState.Success(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        time = location.time,
                        distance = results[0]
                    )
                } else {
                    locationState = LocationState.Error("Не вдалося отримати локацію")
                }
            }
            .addOnFailureListener { e ->
                locationState =
                    LocationState.Error(e.localizedMessage ?: "Помилка отримання геолокації")
            }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchLocation()
        } else {
            locationState = LocationState.Error("Дозвіл на геолокацію відхилено")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        if (hasAvatar) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(avatarFile)
                    .memoryCacheKey(avatarImageKey)
                    .diskCacheKey(avatarImageKey)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Профіль",
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (permissionState) {
            "GRANTED" -> {
                Button(onClick = {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        avatarFile
                    )
                    takePictureLauncher.launch(uri)
                }) {
                    Text(if (hasAvatar) "Оновити фото" else "Зробити фото")
                }
            }

            "RATIONALE", "DENIED_OR_NOT_ASKED" -> {
                Text(
                    text = "Для створення аватару необхідно надати доступ на камеру.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Надати доступ")
                }
            }

            "PERMANENTLY_DENIED" -> {
                Text(
                    text = "Дозвіл на камеру було остаточно відхилено. Ви можете увімкнути його в налаштуваннях.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Button(onClick = {
                    // Програмний перехід до системних налаштувань
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Перейти в налаштування")
                }
            }
        }

        Text(text = "Інформація про додаток", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Назва: Гід по місту")
        Text(text = "Версія: 1.0.0")
        Text(text = "Розробник: Кирило Береговий")

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Привіт, $userName!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = { onNameChange(it) },
            label = { Text("Ім'я користувача") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Divider(modifier = Modifier.padding(vertical = 24.dp))

        Text(text = "Моя геолокація", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Відображення станів
        when (val state = locationState) {
            is LocationState.Idle -> {
                Text(
                    "Натисніть кнопку нижче, щоб отримати координати",
                    textAlign = TextAlign.Center
                )
            }

            is LocationState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text("Отримання координат...")
            }

            is LocationState.Success -> {
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Широта (Lat): ${state.latitude}")
                        Text("Довгота (Lon): ${state.longitude}")
                        Text("Точність: ${state.accuracy} м.")

                        val timeString = SimpleDateFormat(
                            "dd.MM.yyyy HH:mm:ss",
                            Locale.getDefault()
                        ).format(Date(state.time))
                        Text("Оновлено: $timeString")

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ціль: Майдан Незалежності (Київ)", fontWeight = FontWeight.SemiBold)
                        val distText = if (state.distance > 1000) String.format(
                            Locale.US,
                            "%.2f км",
                            state.distance / 1000
                        ) else String.format(Locale.US, "%.0f м", state.distance)
                        Text(
                            "Відстань: $distText",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            is LocationState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Перевіряємо наявність дозволів перед викликом
            val hasFine = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasFine || hasCoarse) {
                fetchLocation()
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text("Оновити локацію")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        userName = "Кирило",
        onNameChange = {},
    )
}