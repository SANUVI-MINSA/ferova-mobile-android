package pe.edu.upc.ferovafamily.presentation.treatment_tracking

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)
private val SoftPink = Color(0xFFF9E8E8)
private val ConfirmedGreenBg = Color(0xFFE6F7F0)
private val ConfirmedGreenText = Color(0xFF0F9D58)
private val OmittedRedBg = Color(0xFFFFEBEE)
private val OmittedRedText = Color(0xFFD32F2F)
private val PureBlack = Color(0xFF000000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentTrackingScreen(
    onNavigateBack: () -> Unit,
    viewModel: TreatmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val historyGrouped = viewModel.doseHistoryGrouped

    LaunchedEffect(uiState) {
        Log.d("TreatmentScreen", "UI State - patientName: ${uiState.patientName}")
        Log.d("TreatmentScreen", "UI State - doses: ${uiState.doseHistory.size}")
        Log.d("TreatmentScreen", "UI State - isLoading: ${uiState.isLoading}")
        Log.d("TreatmentScreen", "UI State - error: ${uiState.errorMessage}")
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Historial de Dosis", color = Crimson, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Crimson)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Crimson)
                }
            }
            uiState.errorMessage != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error: ${uiState.errorMessage}", color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadData() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(Modifier.height(16.dp))

                    ChildMedicationCard(
                        patientName = uiState.patientName,
                        supplementName = uiState.supplementName,
                        quantity = uiState.quantity,
                        dosingHours = uiState.dosingHours
                    )

                    Spacer(Modifier.height(24.dp))

                    if (historyGrouped.isEmpty()) {
                        Text(
                            "No hay historial de dosis aún",
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Mostrar todas las dosis sin agrupar por fecha
                        val allDoses = uiState.doseHistory
                        allDoses.forEach { record ->
                            DoseHistoryItem(
                                time = formatTime(record.scheduledDate),
                                detail = if (record.status == "CONFIRMED")
                                    "Confirmada a las ${formatTime(record.confirmedAt ?: "")}"
                                else
                                    "Omitida — Sin confirmación",
                                status = if (record.status == "CONFIRMED") DoseStatus.Confirmed else DoseStatus.Omitted
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ChildMedicationCard(
    patientName: String,
    supplementName: String,
    quantity: String,
    dosingHours: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier.size(60.dp).background(SoftPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Crimson, modifier = Modifier.size(36.dp))
                }
                Box(
                    modifier = Modifier.size(22.dp).background(Crimson, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Medication, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = patientName.ifBlank { "Paciente" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Crimson)
                Text(text = "$supplementName - ${quantity}mg", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(text = "Horario: $dosingHours", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

enum class DoseStatus { Confirmed, Omitted }

@Composable
private fun DoseHistoryItem(
    time: String,
    detail: String,
    status: DoseStatus
) {
    val (bgColor, textColor, labelText, icon) = when (status) {
        DoseStatus.Confirmed -> DoseItemUI(
            ConfirmedGreenBg, ConfirmedGreenText, "CONFIRMADA", Icons.Default.CheckCircle
        )
        DoseStatus.Omitted -> DoseItemUI(
            OmittedRedBg, OmittedRedText, "OMITIDA", Icons.Default.Cancel
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(bgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PureBlack
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(bgColor, RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = labelText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

data class DoseItemUI(
    val bgColor: Color,
    val textColor: Color,
    val labelText: String,
    val icon: ImageVector
)
private fun formatTime(isoDate: String): String {
    return try {
        val parsed = LocalDateTime.parse(isoDate)
        parsed.format(DateTimeFormatter.ofPattern("hh:mm a"))
    } catch (e: Exception) {
        "08:00 AM"
    }
}