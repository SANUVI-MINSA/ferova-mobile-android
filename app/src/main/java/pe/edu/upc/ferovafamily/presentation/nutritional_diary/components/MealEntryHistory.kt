package pe.edu.upc.ferovafamily.presentation.nutritional_diary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodEntry
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodItem
import pe.edu.upc.ferovafamily.presentation.theme.Crimson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealEntryHistory(
    patientName: String,
    foodEntries: List<FoodEntry>,
    foodItems: List<FoodItem>,
    modifier: Modifier = Modifier
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }

    val patientEntries = foodEntries.filter { it.patientName == patientName }

    val filteredByDate = patientEntries.filter { entry ->
        if (startDate.isEmpty() || endDate.isEmpty()) true
        else entry.registeredAt in startDate..endDate
    }

    val groupedByDate = filteredByDate
        .groupBy { it.registeredAt }
        .toSortedMap(reverseOrder())

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

        // Lista de cards
        items(groupedByDate.entries.toList()) { (date, entries) ->
            val inhibitorCount = entries.count { entry ->
                foodItems.find { it.id == entry.foodItemId }?.isInhibitor == true
            }
            val totalIron = entries.sumOf { it.ironContributed }

            DayHistoryCard(
                date = date,
                totalIron = totalIron,
                inhibitorCount = inhibitorCount,
                foodCount = entries.size
            )
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
            DatePicker(state = state,
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
    date: String,
    totalIron: Double,
    inhibitorCount: Int,
    foodCount: Int,
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
                    text = yyyyMMddToReadable(date),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f", totalIron)} mg de hierro absorbido",
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
                            if (inhibitorCount > 0) Color(0xFFFFE7E7) else Color(0xFFF0F0F0)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(if (inhibitorCount > 0) R.drawable.warning
                        else R.drawable.check_circle),
                        contentDescription = null,
                        tint = if (inhibitorCount > 0) Color(0xFF8B0000) else Color(0xFF888888),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = when (inhibitorCount) {
                            0 -> "Sin inhibidores"
                            1 -> "1 inhibidor"
                            else -> "$inhibitorCount inhibidores"
                        },
                        fontSize = 11.sp,
                        color = if (inhibitorCount > 0) Color(0xFF8B0000) else Color(0xFF888888)
                    )
                }
                Text(
                    text = "$foodCount alimentos",
                    fontSize = 12.sp,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

// Helpers
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