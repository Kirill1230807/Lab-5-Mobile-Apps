package com.example.lab5mobileapps.presentation.screens

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

@Composable
fun OnBoardingScreenUI(
    savedName: String = "",
    onNavigateToEnterName: () -> Unit,
    onNavigateToMain: (String) -> Unit
) {
    var userName by remember(savedName) { mutableStateOf(savedName) }

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
                onNavigateToMain(userName)
            },
            enabled = userName.isNotBlank(),
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (userName.isNotBlank()) {
                Text("Привіт, $userName! Розпочати")
            } else {
                Text("Розпочати")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun OnBoardingScreenUIPreview() {
    OnBoardingScreenUI(
        savedName = "",
        onNavigateToEnterName = { },
        onNavigateToMain = { }
    )
}