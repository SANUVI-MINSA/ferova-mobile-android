package pe.edu.upc.ferovafamily.presentation.nutritional_diary.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pe.edu.upc.ferovafamily.domain.model.nutrition.FoodItem
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.NutritionalDiaryViewModel

private const val TAG = "RegisterMealDialog"

@Composable
fun RegisterMealDialog(
    foodItem: FoodItem,
    patientId: String,
    viewModel: NutritionalDiaryViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var quantity by remember { mutableStateOf("") }
    var quantityError by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val warning by viewModel.warning.collectAsState()
    val registerResult by viewModel.registerFoodEntryResult.collectAsState()

    Log.d(TAG, "========================================")
    Log.d(TAG, "📋 RegisterMealDialog - INICIO")
    Log.d(TAG, "📋 foodItem: ${foodItem.name} (ID: ${foodItem.foodItemId})")
    Log.d(TAG, "📋 patientId: $patientId")
    Log.d(TAG, "📋 isLoading: $isLoading")
    Log.d(TAG, "📋 error: $error")
    Log.d(TAG, "📋 registerResult: $registerResult")
    Log.d(TAG, "========================================")

    // ✅ MODIFICADO: Esperar un poco antes de cerrar para que el backend procese
    LaunchedEffect(registerResult) {
        if (registerResult != null && error == null) {
            Log.d(TAG, "✅ registerResult recibido con éxito")
            // ✅ Esperar 800ms para que el backend procese el registro
            delay(800)
            Log.d(TAG, "🔄 Recargando diario después de registro")
            viewModel.loadTodayDiary(patientId)
            onSuccess()
            onDismiss()
        }
    }

    fun handleRegister() {
        Log.d(TAG, "🔘 handleRegister - Botón REGISTRAR presionado")

        if (quantity.isEmpty()) {
            Log.d(TAG, "❌ Cantidad vacía")
            quantityError = true
            return
        }

        val qty = quantity.toIntOrNull()

        if (qty == null || qty <= 0) {
            Log.d(TAG, "❌ Cantidad inválida: '$quantity'")
            quantityError = true
            return
        }

        quantityError = false

        Log.d(TAG, "✅ Cantidad válida: $qty")
        Log.d(TAG, "📤 Llamando a viewModel.registerFoodEntry con:")
        Log.d(TAG, "   - patientId: $patientId")
        Log.d(TAG, "   - foodItemId: ${foodItem.foodItemId}")
        Log.d(TAG, "   - quantity: $qty")

        viewModel.registerFoodEntry(
            patientId = patientId,
            foodItemId = foodItem.foodItemId,
            quantity = qty
        )

        Log.d(TAG, "📤 viewModel.registerFoodEntry llamado")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = foodItem.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B0000)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                if (foodItem.isInhibitor) {
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFE7E7))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8B0000)
                                    )
                                ) {
                                    append("⚠️ ¡Advertencia! ")
                                }
                                withStyle(SpanStyle(color = Color(0xFF8B0000))) {
                                    append("${foodItem.name} puede reducir la absorción del suplemento de hierro.")
                                }
                            },
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Hierro por 100g",
                                fontSize = 12.sp,
                                color = Color(0xFF888888)
                            )
                            Text(
                                text = "${foodItem.ironMgPer100g} mg",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B0000)
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Tipo de Hierro",
                                fontSize = 12.sp,
                                color = Color(0xFF888888)
                            )
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF8B0000))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (foodItem.ironType == "hemo") "Hemo" else "No-Hemo",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Cantidad",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = {
                            quantity = it
                            quantityError = false
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        placeholder = {
                            Text("0", color = Color(0xFFAAAAAA))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFFE7E7),
                            unfocusedContainerColor = Color(0xFFFFE7E7),
                            focusedBorderColor = if (quantityError) Color(0xFFB71C1C) else Color(0xFF8B0000),
                            unfocusedBorderColor = if (quantityError) Color(0xFFB71C1C) else Color(0xFFDDDDDD),
                            cursorColor = Color(0xFF8B0000),
                            focusedTextColor = Color(0xFF1A1A1A),
                            unfocusedTextColor = Color(0xFF1A1A1A)
                        ),
                        singleLine = true,
                        isError = quantityError
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F0F0))
                            .padding(horizontal = 14.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = if (foodItem.foodItemId.let { id ->
                                    id in listOf("FOOD_036", "FOOD_037", "FOOD_038")
                                            || id in listOf("FOOD_039", "FOOD_040", "FOOD_041", "FOOD_042")
                                }) "ml" else "g",
                            fontSize = 13.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }

                if (quantityError) {
                    Text(
                        text = "Ingresa una cantidad válida",
                        fontSize = 12.sp,
                        color = Color(0xFFB71C1C)
                    )
                }

                if (error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFEBEE))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = " ${error ?: "Error desconocido"}",
                            fontSize = 12.sp,
                            color = Color(0xFFB71C1C),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (warning != null && registerResult != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF3E0))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = " ${warning}",
                            fontSize = 12.sp,
                            color = Color(0xFFFF9800),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF8B0000)
                    )
                }
            } else {
                Button(
                    onClick = { handleRegister() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
                    enabled = quantity.isNotEmpty() && !isLoading
                ) {
                    Text(
                        text = "Registrar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        dismissButton = {
            if (!isLoading) {
                Button(
                    onClick = {
                        Log.d(TAG, "🔘 Botón CANCELAR presionado")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text(
                        text = "Cancelar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B0000)
                    )
                }
            }
        }
    )
}