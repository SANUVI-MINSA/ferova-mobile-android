package pe.edu.upc.ferovafamily.presentation.consultations.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.presentation.consultations.ConsultationsViewModel
import pe.edu.upc.ferovafamily.presentation.consultations.components.NurseHeader

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewConsultationScreen(
    childId: String,
    onBack: () -> Unit,
    onConsultationCreated: (consultationId: String) -> Unit,
    viewModel: ConsultationsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val patient = remember(state.patients) { viewModel.getPatientById(childId) }
    val nurse = patient?.nurse
    var messageText by remember { mutableStateOf("") }

    // Navegación reactiva: cuando la consulta se crea en el backend, vamos al chat.
    LaunchedEffect(state.createdConsultationId) {
        state.createdConsultationId?.let { id ->
            viewModel.consumeCreatedConsultation()
            onConsultationCreated(id)
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Consulta para ${patient?.patientName ?: ""}",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Cream,
                    titleContentColor = Crimson,
                    navigationIconContentColor = Crimson
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Tu mensaje será enviado directamente a la " +
                        "especialista asignada para el seguimiento de ${patient?.patientName ?: ""}.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(Modifier.height(16.dp))

            nurse?.let { NurseHeader(nurse = it) }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Tu mensaje",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp),
                placeholder = { Text("Escribe aquí tus dudas sobre ${patient?.patientName ?: ""}") },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Crimson,
                    cursorColor = Crimson
                )
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.createConsultation(childId, messageText.trim()) },
                enabled = messageText.isNotBlank() && nurse != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Crimson,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Enviar Consulta", color = Color.White)
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Esta comunicación es privada y segura",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
