package com.example.lab5mobileapps.presentation.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lab5mobileapps.domain.model.Place

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceDialog(
    onDismiss: () -> Unit,
    onSave: (Place) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    // Значення полів
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }
    var isFavourite by remember { mutableStateOf(false) }

    // Відстеження фокусу
    var nameTouched by remember { mutableStateOf(false) }
    var nameWasFocused by remember { mutableStateOf(false) }
    var descTouched by remember { mutableStateOf(false) }
    var descWasFocused by remember { mutableStateOf(false) }
    var categoryTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var emailWasFocused by remember { mutableStateOf(false) }
    var priceTouched by remember { mutableStateOf(false) }
    var priceWasFocused by remember { mutableStateOf(false) }

    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    val nameError = if (nameTouched && name.length < 3) "Мінімум 3 символи" else null
    val descError = if (descTouched && desc.isBlank()) "Опис не може бути порожнім" else null
    val categoryError = if (categoryTouched && category.isBlank()) "Оберіть категорію" else null
    val emailError = if (emailTouched && !email.matches(emailPattern)) "Некоректний формат email" else null
    val priceValue = price.toDoubleOrNull()
    val priceError = if (priceTouched && (priceValue == null || priceValue <= 0)) "Ціна має бути > 0" else null

    val isFormValid by remember {
        derivedStateOf {
            name.length >= 3 &&
                    desc.isNotBlank() &&
                    category.isNotBlank() &&
                    email.matches(emailPattern) &&
                    (price.toDoubleOrNull() ?: 0.0) > 0
        }
    }

    var expandedCategory by remember { mutableStateOf(false) }
    val categories = listOf("Природа", "Архітектура", "Музеї", "Ресторани", "Загальна")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    })
                }
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = if (isTablet) {
                    Modifier
                        .width(600.dp)
                        .heightIn(max = (configuration.screenHeightDp * 0.9f).dp)
                        .padding(vertical = 24.dp)
                } else {
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = if (isTablet) 8.dp else 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Додати нове місце", style = MaterialTheme.typography.headlineMedium)

                    Text("Основна інформація", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    HorizontalDivider()

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Назва") },
                        isError = nameError != null,
                        supportingText = { if (nameError != null) Text(nameError) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (it.isFocused) nameWasFocused = true
                                else if (nameWasFocused) nameTouched = true
                            },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
                    )

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Опис") },
                        isError = descError != null,
                        supportingText = { if (descError != null) Text(descError) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (it.isFocused) descWasFocused = true
                                else if (descWasFocused) descTouched = true
                            },
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = {
                            expandedCategory = !expandedCategory
                            if (!expandedCategory) categoryTouched = true
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Категорія") },
                            isError = categoryError != null,
                            supportingText = { if (categoryError != null) Text(categoryError) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = {
                                expandedCategory = false
                                categoryTouched = true
                            }
                        ) {
                            categories.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        category = selectionOption
                                        expandedCategory = false
                                        categoryTouched = true
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Додаткові параметри", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    HorizontalDivider()

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Контактний Email") },
                        isError = emailError != null,
                        supportingText = { if (emailError != null) Text(emailError) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (it.isFocused) emailWasFocused = true
                                else if (emailWasFocused) emailTouched = true
                            },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
                    )

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Середня ціна (₴)") },
                        isError = priceError != null,
                        supportingText = { if (priceError != null) Text(priceError) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (it.isFocused) priceWasFocused = true
                                else if (priceWasFocused) priceTouched = true
                            },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        })
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Рейтинг: ${"%.1f".format(rating)}")
                        Slider(
                            value = rating,
                            onValueChange = { rating = it },
                            valueRange = 0f..5f,
                            steps = 9
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Додати до улюблених")
                        Switch(
                            checked = isFavourite,
                            onCheckedChange = { isFavourite = it }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Скасувати") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (isFormValid) {
                                    val newPlace = Place(
                                        name = name,
                                        description = desc,
                                        category = category,
                                        isFavourite = isFavourite,
                                        rating = rating.toDouble(),
                                        price = price.toDouble(),
                                        contactEmail = email,
                                        imageRes = 0
                                    )
                                    onSave(newPlace)
                                }
                            },
                            enabled = isFormValid
                        ) {
                            Text("Зберегти")
                        }
                    }
                }
            }
        }
    }
}