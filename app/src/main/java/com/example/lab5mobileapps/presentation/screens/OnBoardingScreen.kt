package com.example.lab5mobileapps.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.lab5mobileapps.R
import com.example.lab5mobileapps.presentation.ui.theme.AppTheme

@Composable
fun OnBoardingScreenUI(
    savedName: String = "",
    onNavigateToEnterName: () -> Unit,
    onNavigateToMain: (String) -> Unit
) {
    var userName by remember(savedName) { mutableStateOf(savedName) }

    LaunchedEffect(savedName) {
        if (savedName.isNotBlank()) {
            onNavigateToMain(savedName)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoadditional),
                contentDescription = null,
                modifier = Modifier.size(350.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Гід по місту",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToEnterName,
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Ввести ім'я")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onNavigateToMain(savedName)
            },
            enabled = userName.isNotBlank(),
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (savedName.isNotBlank()) {
                Text("Привіт, $savedName! Розпочати")
            } else {
                Text("Розпочати")
            }
        }
    }
}

@Preview(showSystemUi = true, name = "Light Mode")
@Composable
private fun OnBoardingScreenUIPreview() {
    AppTheme {
        OnBoardingScreenUI(
            savedName = "",
            onNavigateToEnterName = { },
            onNavigateToMain = { }
        )
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun OnBoardingScreenUIPreviewDarkMode() {
    AppTheme {
        OnBoardingScreenUI(
            savedName = "",
            onNavigateToEnterName = { },
            onNavigateToMain = { }
        )
    }
}