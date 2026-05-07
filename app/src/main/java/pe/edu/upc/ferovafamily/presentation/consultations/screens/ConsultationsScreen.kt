package pe.edu.upc.ferovafamily.presentation.consultations.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.presentation.consultations.ConsultationsViewModel
import pe.edu.upc.ferovafamily.presentation.consultations.components.ChildCard
import androidx.compose.material.icons.filled.LocationOn

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)

@Composable
fun ConsultationsScreen(
    onWriteForChild: (childId: String) -> Unit,
    onOpenChat: (consultationId: String) -> Unit,
    onGoToMyConsultations: () -> Unit,
    onSeeNearbyHealthCenters: () -> Unit,
    viewModel: ConsultationsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Cream
    ) {
        if (!state.hasNurse) {
            EmptyNurseState(onSeeNearbyHealthCenters = onSeeNearbyHealthCenters)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "Nueva Consulta",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Crimson
                )
                Text(
                    text = "¿Sobre quién es tu consulta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(Modifier.height(16.dp))

                state.children.forEach { child ->
                    ChildCard(
                        child = child,
                        onWriteConsultation = {
                            val existing = viewModel.getOpenConsultationFor(child.id)
                            if (existing != null) onOpenChat(existing.id)
                            else onWriteForChild(child.id)
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onGoToMyConsultations,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Mis Consultas", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun EmptyNurseState(
    onSeeNearbyHealthCenters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ícono grande de chat con búsqueda (placeholder)
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(Color.White, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                tint = Crimson,
                modifier = Modifier.size(72.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Aún no tienes una enfermera\nasignada",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Para iniciar una consulta, primero la enfermera debe " +
                    "asignar a su pequeño en su lista de pacientes a atender. " +
                    "Te recomendamos primero solicitar una cita en una posta " +
                    "cercana y luego a dicho enfermero consultarle para que le " +
                    "asigne en si. Si ya fue una posta espera al enfermero que " +
                    "lo asigne.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onSeeNearbyHealthCenters,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Crimson),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                androidx.compose.material.icons.Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Ver Postas Cercanas",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}