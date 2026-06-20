package pe.edu.upc.ferovafamily.presentation.nutritional_diary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.domain.model.nutrition.DaySummary
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.NutritionalDiaryViewModel
import pe.edu.upc.ferovafamily.presentation.theme.Crimson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealEntryHistory(
    patientId: String,
    viewModel: NutritionalDiaryViewModel,
    modifier: Modifier = Modifier
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }

    // ════════════════════════════════════════════════════════════════════════
    // ESTADOS DEL VIEWMODEL
    // ════════════════════════════════════════════════════════════════════════

    val nutritionalHistory by viewModel.nutritionalHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val daySummaries = nutritionalHistory?.days ?: emptyList()

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.tune),
                    contentDescription = null,
                    tint = Color(0xFF1A1A1A),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Buscar por fecha",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }
        }

        item {
            // Selectors de fecha
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = if (dateError) 4.dp else 16.dp)
            ) {
                DateInputChip(
                    date = startDate,
                    onClick = { showStartPicker = true },
                    modifier = Modifier.weight(1f)
                )
                Text(text = "–", fontSize = 16.sp, color = Color(0xFFD24E4E))
                DateInputChip(
                    date = endDate,
                    onClick = { showEndPicker = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (dateError) {
            item {
                Text(
                    text = "La fecha inicio no puede ser mayor a la de fin",
                    fontSize = 12.sp,
                    color = Color(0xFF8B0000),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        // ════════════════════════════════════════════════════════════════════
        // MOSTRAR ESTADO
        // ════════════════════════════════════════════════════════════════════

        when {
            isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF8B0000)
                        )
                    }
                }
            }

            error != null -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "❌ ${error ?: "Error desconocido"}",
                            fontSize = 14.sp,
                            color = Color(0xFFB71C1C),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            daySummaries.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sin registros en este período",
                            fontSize = 14.sp,
                            color = Color(0xFF888888)
                        )
                    }
                }
            }

            else -> {
                // Lista de cards con el historial
                items(daySummaries) { daySummary ->
                    DayHistoryCard(
                        daySummary = daySummary
                    )
                }
            }
        }
    }

    // Start DatePicker
    if (showStartPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val picked = millisToYYYYMMDD(millis)
                        if (endDate.isNotEmpty() && picked > endDate) {
                            dateError = true
                        } else {
                            dateError = false
                            startDate = picked
                        }
                    }
                    showStartPicker = false
                }) { Text("Aceptar", color = Color(0xFF8B0000)) }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) {
                    Text("Cancelar", color = Color(0xFF8B0000))
                }
            },
        ) {
            DatePicker(
                state = state,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Crimson,
                    selectedDayContentColor = Color.White,
                    todayDateBorderColor = Crimson,
                    todayContentColor = Crimson
                )
            )
        }
    }

    // End DatePicker
    if (showEndPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val picked = millisToYYYYMMDD(millis)
                        if (startDate.isNotEmpty() && picked < startDate) {
                            dateError = true
                        } else {
                            dateError = false
                            endDate = picked
                        }
                    }
                    showEndPicker = false
                }) { Text("Aceptar", color = Color(0xFF8B0000)) }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) {
                    Text("Cancelar", color = Color(0xFF8B0000))
                }
            },
        ) {
            DatePicker(
                state = state,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Crimson,
                    selectedDayContentColor = Color.White,
                    todayDateBorderColor = Crimson,
                    todayContentColor = Crimson
                )
            )
        }
    }
}

@Composable
fun DateInputChip(
    date: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFFE7E7))
            .border(1.dp, Color(0xFFD24E4E), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.calendar_month),
            contentDescription = null,
            tint = Color(0xFF8B0000),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = if (date.isEmpty()) "DD/MM/YYYY" else yyyyMMddToDDMMYYYY(date),
            fontSize = 13.sp,
            color = if (date.isEmpty()) Color(0xFFAAAAAA) else Color(0xFF1A1A1A)
        )
    }
}

@Composable
fun DayHistoryCard(
    daySummary: DaySummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fecha e hierro
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = daySummary.displayDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f", daySummary.totalIronAbsorbed)} mg de hierro absorbido",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B0000)
                )
            }

            // Badge + cantidad alimentos
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (daySummary.hasInhibitor) Color(0xFFFFE7E7) else Color(0xFFF0F0F0)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (daySummary.hasInhibitor) R.drawable.warning
                            else R.drawable.check_circle
                        ),
                        contentDescription = null,
                        tint = if (daySummary.hasInhibitor) Color(0xFF8B0000) else Color(0xFF888888),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = when (daySummary.inhibitorCount) {
                            0 -> "Sin inhibidores"
                            1 -> "1 inhibidor"
                            else -> "${daySummary.inhibitorCount} inhibidores"
                        },
                        fontSize = 11.sp,
                        color = if (daySummary.hasInhibitor) Color(0xFF8B0000) else Color(0xFF888888)
                    )
                }
                Text(
                    text = when (daySummary.totalFoodEntries) {
                        0 -> "Sin alimentos"
                        1 -> "1 alimento"
                        else -> "${daySummary.totalFoodEntries} alimentos"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// HELPERS
// ═══════════════════════════════════════════════════════════════════════════

fun millisToYYYYMMDD(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(millis))
}

fun yyyyMMddToDDMMYYYY(date: String): String {
    val parts = date.split("-")
    return "${parts[2]}/${parts[1]}/${parts[0]}"
}

fun yyyyMMddToReadable(date: String): String {
    val months = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    val parts = date.split("-")
    val day = parts[2].toInt()
    val month = months[parts[1].toInt() - 1]
    return "$day de $month"
}