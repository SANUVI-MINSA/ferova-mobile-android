package pe.edu.upc.ferovafamily.presentation.appointments.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.presentation.appointments.AppointmentsViewModel
import pe.edu.upc.ferovafamily.domain.model.appointments.TimeSlot
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)
private val SoftPink = Color(0xFFF9E8E8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotSelectionScreen(
    centerId: String,
    patientId: String,
    dateIso: String,
    onBack: () -> Unit,
    onConfirm: (appointmentId: String) -> Unit,
    viewModel: AppointmentsViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()
    val patient = remember(state.patients) {
        state.patients.find { it.id == patientId }
    }
    val date = remember(dateIso) { LocalDate.parse(dateIso) }
    var selectedSlot by remember { mutableStateOf<TimeSlot?>(null) }

    Log.d("TimeSlot", "Patient id: $patientId, info: $patient")

    LaunchedEffect(Unit) {
        viewModel.loadAvailableSlots(centerId, date)
        viewModel.getPatients()
        viewModel.getCenterById(centerId)
    }

    LaunchedEffect(state.bookingSuccess) {
        state.bookingSuccess?.let { confirmedId ->
            onConfirm(confirmedId) // navega cuando el state se actualice
        }
    }

    state.error?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { },
            containerColor = Color.White,
            title = {
                Text(
                    "No se pudo reservar",
                    fontWeight = FontWeight.Bold,
                    color = Crimson
                )
            },
            text = {
                Text(
                    text = errorMessage,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearError()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al inicio", color = Color.White)
                }
            }
        )
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Selecciona horario", fontWeight = FontWeight.Bold) },
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
                        selectedSlot?.let { slot ->
                            viewModel.bookAppointment(
                                centerId = centerId,
                                centerName = state.selectedCenter?.name ?: "",
                                patientId = patientId,
                                patientName = patient?.name ?: "",
                                date = date,
                                time = slot.time
                            )
                        }
                    },
                    enabled = selectedSlot != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Crimson,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Confirmar Reserva →",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    ) { padding ->

        if (state.isLoadingPatients || state.isLoadingSlots || state.isLoadingCenter) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Crimson)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Card de fecha seleccionada
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(SoftPink, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Crimson
                            )
                            Text(
                                text = date.month
                                    .getDisplayName(java.time.format.TextStyle.SHORT, Locale("es"))
                                    .uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Crimson
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Fecha Selecionada",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(
                            text = date.format(
                                DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es"))
                            ).replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    TextButton(onClick = onBack) {
                        Text("Editar", color = Crimson, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Horarios disponibles",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Spacer(Modifier.height(12.dp))

            // Grid de slots: 2 columnas
            val rows = state.availableSlots.chunked(2)
            rows.forEach { rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowSlots.forEach { slot ->
                        SlotCard(
                            slot = slot,
                            isSelected = selectedSlot == slot,
                            onClick = {
                                if (slot.isAvailable) selectedSlot = slot
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowSlots.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SlotCard(
    slot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = when {
        !slot.isAvailable -> Color(0xFFEDEDED)
        isSelected -> Crimson
        else -> Color.White
    }
    val textColor = when {
        !slot.isAvailable -> Color.LightGray
        isSelected -> Color.White
        else -> Color.DarkGray
    }

    Card(
        modifier = modifier
            .height(70.dp)
            .clickable(enabled = slot.isAvailable, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(12.dp),
        border = if (!isSelected && slot.isAvailable)
            androidx.compose.foundation.BorderStroke(1.dp, SoftPink) else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = slot.time,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            if (!slot.isAvailable) {
                Text(
                    text = "Ocupado",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}