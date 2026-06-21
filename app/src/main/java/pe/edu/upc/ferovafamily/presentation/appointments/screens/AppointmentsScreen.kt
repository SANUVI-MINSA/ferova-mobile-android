package pe.edu.upc.ferovafamily.presentation.appointments.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.domain.model.Patient
import pe.edu.upc.ferovafamily.presentation.appointments.AppointmentsViewModel
import pe.edu.upc.ferovafamily.domain.model.appointments.Appointment
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Crimson = Color(0xFFB71C1C)
private val SoftPink = Color(0xFFFCE4EC)
private val Cream = Color(0xFFFFF8F8)
private val SuccessGreen = Color(0xFF2E7D32)
private val CancelRed = Color(0xFFB71C1C)

fun formatAppointmentDate(dateStr: String, timeStr: String): String {
    return try {
        val date = LocalDate.parse(dateStr)
        val time = LocalTime.parse(timeStr.padStart(5, '0'))
        val datePart = date.format(
            DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", Locale("es"))
        ).replaceFirstChar { it.uppercase() }
        val hour = time.hour
        val minute = time.minute
        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val minuteStr = if (minute == 0) "00" else minute.toString().padStart(2, '0')
        "$datePart • $hour12:$minuteStr $amPm"
    } catch (_: Exception) {
        "$dateStr • $timeStr"
    }
}

fun formatNextDate(dateStr: String): String {
    return try {
        val date = LocalDate.parse(dateStr)
        date.format(DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es")))
            .replaceFirstChar { it.uppercase() }
    } catch (_: Exception) {
        dateStr
    }
}

fun formatTime12h(timeStr: String): String {
    return try {
        val time = LocalTime.parse(timeStr.padStart(5, '0'))
        val hour = time.hour
        val minute = time.minute
        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val minuteStr = if (minute == 0) "00" else minute.toString().padStart(2, '0')
        "$hour12:$minuteStr $amPm"
    } catch (_: Exception) {
        timeStr
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    onScheduleAppointment: () -> Unit,
    viewModel: AppointmentsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // ✅ CORREGIDO: Usar state.patients directamente para la selección
    // Si no hay pacientes, selectedPatientId es ""
    val patients = state.patients
    var selectedPatientId by remember { mutableStateOf("") }

    // ✅ Actualizar selectedPatientId cuando cambien los pacientes
    LaunchedEffect(patients) {
        if (patients.isNotEmpty() && selectedPatientId.isEmpty()) {
            selectedPatientId = patients.first().id
        } else if (patients.isEmpty()) {
            selectedPatientId = ""
        }
    }

    var cancelingPatientId by remember { mutableStateOf("") }
    var showCancelDialog by remember { mutableStateOf(false) }
    var appointmentToCancel by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getPatients()
        viewModel.loadNextAppointment()
    }

    LaunchedEffect(selectedPatientId) {
        if (selectedPatientId.isNotEmpty()) {
            viewModel.loadAppointmentHistory(selectedPatientId)
        }
    }

    LaunchedEffect(state.cancelMessage) {
        state.cancelMessage?.let {
            viewModel.loadNextAppointment()
            if (cancelingPatientId == selectedPatientId) {
                viewModel.loadAppointmentHistory(selectedPatientId)
            }
            viewModel.clearCancelMessage()
        }
    }

    val nextAppointmentWithName = remember(state.nextAppointment, state.patients) {
        state.nextAppointment?.let { appointment ->
            val patientName = state.patients
                .find { it.id == appointment.patientId }
                ?.name ?: ""
            appointment.copy(patientName = patientName)
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(SoftPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Crimson,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "¿Cancelar cita?",
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    "Estás a punto de cancelar tu consulta.\n¿Deseas continuar?",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            cancelingPatientId = state.nextAppointment?.patientId ?: ""
                            showCancelDialog = false
                            viewModel.clearNextAppointment()
                            viewModel.cancelAppointment(appointmentToCancel)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Crimson),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !state.isCancelingAppointment
                    ) {
                        if (state.isCancelingAppointment) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Sí, cancelar cita →",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = { showCancelDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, Color.LightGray
                        )
                    ) {
                        Text("No, mantener cita", color = Color.DarkGray)
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        )
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Citas",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Crimson
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ✅ SELECTOR DE PACIENTES - Usa la lista actualizada
            item {
                PatientSelectorSection(
                    patients = state.patients,
                    selectedPatientId = selectedPatientId,
                    onPatientSelected = { selectedPatientId = it }
                )
            }

            item {
                Text(
                    "Cita Actual",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
                Spacer(Modifier.height(8.dp))
                CurrentAppointmentSection(
                    isLoading = state.isLoadingNextAppointment,
                    appointment = nextAppointmentWithName,
                    onSchedule = onScheduleAppointment,
                    onCancel = { id ->
                        appointmentToCancel = id
                        showCancelDialog = true
                    }
                )
            }

            item {
                Text(
                    "Historial de Citas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
                Spacer(Modifier.height(8.dp))
            }

            if (state.isLoadingAppointmentHistory) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Crimson)
                    }
                }
            } else if (state.appointmentHistory.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No se han encontrado citas en el historial.",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(state.appointmentHistory) { appointment ->
                    AppointmentHistoryItem(appointment = appointment)
                    Spacer(Modifier.height(4.dp))
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ── Selector de pacientes ─────────────────────────────────────
@Composable
fun PatientSelectorSection(
    patients: List<Patient>,
    selectedPatientId: String,
    onPatientSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Seleccionar Paciente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Spacer(Modifier.height(12.dp))

            // ✅ USAR LAZYROW PARA SCROLL HORIZONTAL
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(patients) { patient ->
                    val isSelected = patient.id == selectedPatientId
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onPatientSelected(patient.id)
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) SoftPink else Color(0xFFF5F5F5))
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) Crimson else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isSelected) Crimson else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = patient.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Crimson else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// ── Sección cita actual ───────────────────────────────────────
@Composable
fun CurrentAppointmentSection(
    isLoading: Boolean,
    appointment: Appointment?,
    onSchedule: () -> Unit,
    onCancel: (String) -> Unit
) {
    if (isLoading) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Crimson,
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.5.dp
                )
            }
        }
        return
    }

    if (appointment == null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SoftPink)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "No tienes citas programadas próximamente.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onSchedule,
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        "Agendar nueva cita >",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Crimson),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "PRÓXIMA FECHA",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = formatTime12h(appointment.time),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatNextDate(appointment.date.toString()),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Paciente",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = appointment.patientName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Sede",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = appointment.healthCenterName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { onCancel(appointment.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, Color.White.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        "Cancelar Cita",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ── Item del historial ────────────────────────────────────────
@Composable
fun AppointmentHistoryItem(appointment: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(SoftPink, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = Crimson,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = appointment.healthCenterName,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (appointment.isConfirmed) Color(0xFFE8F5E9)
                                else Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (appointment.isConfirmed) SuccessGreen.copy(alpha = 0.3f)
                                else CancelRed.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (appointment.isConfirmed) "CONFIRMADA" else "CANCELADA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (appointment.isConfirmed) SuccessGreen else CancelRed
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Paciente: ${appointment.patientName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = formatAppointmentDate(
                            appointment.date.toString(),
                            appointment.time
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}