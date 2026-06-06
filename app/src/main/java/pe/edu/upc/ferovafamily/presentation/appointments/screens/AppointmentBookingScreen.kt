package pe.edu.upc.ferovafamily.presentation.appointments.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale


private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)
private val SoftPink = Color(0xFFF9E8E8)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    centerId: String,
    onBack: () -> Unit,
    onContinue: (patientId: String, dateIso: String) -> Unit
) {

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Reserva Cita", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Crimson
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Crimson
                )
            )
        },
        bottomBar = {
            Surface(color = Cream) {
                Button(
                    onClick = {
                        selectedDate?.let {
                            onContinue(selectedPatient.id, it.toString())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = selectedDate != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Crimson,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Continuar →",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Selecionar Paciente", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                patientsMock.forEach { patient ->
                    PatientAvatar(
                        patient = patient,
                        isSelected = patient.id == selectedPatient.id,
                        onClick = { selectedPatient = patient }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Header del mes
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentMonth.format(
                        DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es"))
                    ).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Mes anterior",
                        tint = Crimson
                    )
                }
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Mes siguiente",
                        tint = Crimson
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            CalendarGrid(
                month = currentMonth,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )
        }
    }
}

@Composable
private fun PatientAvatar(
    patient: Patient,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(SoftPink, CircleShape)
                .then(
                    if (isSelected) Modifier.border(2.dp, Crimson, CircleShape)
                    else Modifier
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Crimson,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = patient.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Crimson else Color.DarkGray
        )
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val firstDay = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()
    // Lunes = 1, Domingo = 7 → desplazamiento para que la primera fila empiece en Lunes
    val offset = (firstDay.dayOfWeek.value - 1)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Encabezados de días
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("LU", "MA", "MI", "JU", "VI", "SA", "DO").forEachIndexed { i, dn ->
                    Text(
                        text = dn,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (i >= 5) Crimson.copy(alpha = 0.6f) else Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            val totalCells = offset + daysInMonth
            val rows = (totalCells + 6) / 7

            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - offset + 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayNumber in 1..daysInMonth) {
                                val date = month.atDay(dayNumber)
                                val isPast = date.isBefore(today)
                                val isWeekend = col >= 5
                                val isSelected = selectedDate == date
                                val isClickable = !isPast && !isWeekend

                                DayCell(
                                    day = dayNumber,
                                    isSelected = isSelected,
                                    isEnabled = isClickable,
                                    isWeekend = isWeekend,
                                    onClick = { if (isClickable) onDateSelected(date) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    isEnabled: Boolean,
    isWeekend: Boolean,
    onClick: () -> Unit
) {
    val bg = when {
        isSelected -> Crimson
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> Color.White
        !isEnabled -> Color.LightGray
        isWeekend -> Crimson.copy(alpha = 0.5f)
        else -> Color.DarkGray
    }
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(bg, RoundedCornerShape(8.dp))
            .clickable(enabled = isEnabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}