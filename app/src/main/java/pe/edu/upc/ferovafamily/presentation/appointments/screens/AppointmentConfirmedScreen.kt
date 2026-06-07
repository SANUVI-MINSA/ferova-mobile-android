package pe.edu.upc.ferovafamily.presentation.appointments.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.presentation.appointments.AppointmentsViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)
private val SoftPink = Color(0xFFF9E8E8)

@Composable
fun AppointmentConfirmedScreen(
    onBackToHome: () -> Unit,
    viewModel: AppointmentsViewModel
) {

    val state by viewModel.state.collectAsState()
    val appointment = state.appointment

    Surface(modifier = Modifier.fillMaxSize(), color = Cream) {
        if (appointment == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cita no encontrada", color = Color.Gray)
            }
            return@Surface
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Crimson, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "¡Cita Confirmada!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Crimson
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Tu cita ha sido agendad con exito en\nnuestro sistema.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Card resumen
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftPink)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(
                        icon = Icons.Default.LocalHospital,
                        label = "CENTRO MEDICO",
                        value = appointment.healthCenterName
                    )
                    HorizontalDivider(
                        color = SoftPink,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        DetailRow(
                            icon = Icons.Default.CalendarMonth,
                            label = "FECHA",
                            value = appointment.date.format(
                                DateTimeFormatter.ofPattern("d 'de' MMMM,\nyyyy", Locale("es"))
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        DetailRow(
                            icon = Icons.Default.AccessTime,
                            label = "HORA",
                            value = appointment.time,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(
                        color = SoftPink,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    DetailRow(
                        icon = Icons.Default.Person,
                        label = "PACIENTE",
                        value = appointment.patientName
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Crimson),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Volver al Inicio",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(SoftPink, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Crimson, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
        }
    }
}