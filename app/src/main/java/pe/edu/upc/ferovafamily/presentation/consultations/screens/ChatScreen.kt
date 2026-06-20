package pe.edu.upc.ferovafamily.presentation.consultations.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import pe.edu.upc.ferovafamily.presentation.consultations.ConsultationsViewModel
import pe.edu.upc.ferovafamily.presentation.consultations.components.MessageBubble

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    consultationId: String,
    onBack: () -> Unit,
    viewModel: ConsultationsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val consultation = state.consultations.firstOrNull { it.id == consultationId }
    val isOpen = consultation?.isOpen != false
    var draft by remember { mutableStateOf("") }
    var closedDialogDismissed by remember(consultationId) { mutableStateOf(false) }
    var notFoundDialogDismissed by remember(consultationId) { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // ✅ CONTADOR DE INTENTOS FALLIDOS
    var failedAttempts by remember { mutableStateOf(0) }

    Log.d("ChatScreen", "Consultation: $consultationId, isOpen: $isOpen, messages: ${consultation?.messages?.size ?: 0}")

    // ✅ CARGAR EL CHAT + POLLING
    LaunchedEffect(consultationId) {
        Log.d("ChatScreen", "Cargando chat para $consultationId")
        viewModel.loadChat(consultationId)

        while (true) {
            delay(5000)
            Log.d("ChatScreen", "Polling: refrescando mensajes...")
            viewModel.loadChat(consultationId)
        }
    }

    LaunchedEffect(consultation?.messages?.size) {
        consultation?.let {
            if (it.messages.isNotEmpty()) {
                listState.animateScrollToItem(it.messages.size - 1)
            }
        }
    }

    // ✅ DIALOG PARA CONSULTA NO ENCONTRADA - SOLO DESPUÉS DE 3 INTENTOS FALLIDOS
    if (consultation == null && failedAttempts >= 3 && !notFoundDialogDismissed) {
        AlertDialog(
            onDismissRequest = {
                notFoundDialogDismissed = true
                onBack()
            },
            confirmButton = {
                TextButton(onClick = {
                    notFoundDialogDismissed = true
                    onBack()
                }) {
                    Text("Entendido", color = Crimson)
                }
            },
            title = { Text("Consulta no disponible") },
            text = { Text("Esta consulta ya no está activa. Es posible que haya sido cerrada por la enfermera.") }
        )
    }

    // ✅ DIALOG PARA CONSULTA CERRADA
    if (consultation != null && !isOpen && !closedDialogDismissed) {
        AlertDialog(
            onDismissRequest = {
                closedDialogDismissed = true
                onBack()
            },
            confirmButton = {
                TextButton(onClick = {
                    closedDialogDismissed = true
                    onBack()
                }) {
                    Text("Entendido", color = Crimson)
                }
            },
            title = { Text("Esta consulta está cerrada") },
            text = { Text("Para seguir conversando con la enfermera debes crear una nueva consulta.") }
        )
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = consultation?.nurse?.name ?: "Consulta",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Surface(
                            color = if (consultation != null && isOpen) Crimson else Color.Gray,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = if (consultation != null && isOpen) "SINCRONIZADO CON CLOUD" else "CONSULTA NO DISPONIBLE",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
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
        },
        bottomBar = {
            if (consultation != null && isOpen) {
                ChatInputBar(
                    value = draft,
                    onValueChange = { draft = it },
                    onSend = {
                        if (draft.isNotBlank()) {
                            Log.d("ChatScreen", "Enviando mensaje: ${draft.take(50)}...")
                            viewModel.sendMessage(consultationId, draft)
                            draft = ""
                        }
                    }
                )
            } else {
                ClosedConsultationBar()
            }
        }
    ) { padding ->
        if (consultation == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Crimson)
                    Spacer(Modifier.height(16.dp))
                    Text("Cargando consulta...", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(consultation.messages, key = { it.id }) { message ->
                    MessageBubble(message = message)
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(color = Cream, tonalElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje…") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Crimson,
                    cursorColor = Crimson
                ),
                maxLines = 4
            )
            Spacer(Modifier.width(8.dp))
            FilledIconButton(
                onClick = onSend,
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Crimson),
                enabled = value.isNotBlank()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun ClosedConsultationBar() {
    Surface(color = Cream, tonalElevation = 4.dp) {
        Text(
            text = "Esta consulta ya no está disponible.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        )
    }
}