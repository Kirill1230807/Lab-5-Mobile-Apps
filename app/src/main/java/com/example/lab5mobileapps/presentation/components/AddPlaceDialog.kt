package com.example.lab5mobileapps.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.lab5mobileapps.domain.model.Place

@Composable
fun AddPlaceDialog(
    onDismiss: () -> Unit,
    onSave: (Place) -> Unit
) {
    // Стан для збереження введеного тексту
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss, // Закриття діалогу при кліку поза ним
        title = { Text("Додати нове місце") },
        text = {
            // Колонка для розміщення текстових полів одне під одним
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Назва") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Опис") }
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Категорія") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Створюємо новий об'єкт Place з введених даних
                    val newPlace = Place(
                        name = name,
                        description = desc,
                        category = category.ifBlank { "Загальна" },
                        imageRes = 0 // Зображення поки що заглушка (оскільки API зазвичай приймає URL)
                    )
                    onSave(newPlace) // Передаємо створений об'єкт вище
                },
                // Кнопка активна лише якщо поля назви та опису не порожні
                enabled = name.isNotBlank() && desc.isNotBlank()
            ) {
                Text("Зберегти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}