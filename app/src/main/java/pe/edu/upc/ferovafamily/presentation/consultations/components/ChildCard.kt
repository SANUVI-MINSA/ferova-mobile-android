package pe.edu.upc.ferovafamily.presentation.consultations.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upc.ferovafamily.domain.model.communication.PatientWithNurse

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)

@Composable
fun ChildCard(
    patient: PatientWithNurse,
    onWriteConsultation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Cream, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Crimson
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = patient.patientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = patient.nurse?.name ?: "Sin enfermera asignada",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (patient.hasNurse) Color.Gray else Crimson
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onWriteConsultation,
                enabled = patient.hasNurse,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Crimson,
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (patient.hasNurse) "Escribir Consulta" else "Espera la asignación",
                    color = Color.White
                )
            }
        }
    }
}
