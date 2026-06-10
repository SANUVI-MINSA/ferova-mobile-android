package pe.edu.upc.ferovafamily.presentation.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.domain.model.TodayDose

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)
private val SoftPink = Color(0xFFF9E8E8)
private val DisabledGray = Color(0xFFB8B8B8)
private val SuccessGreen = Color(0xFF4CAF50)
private val CancelRed = Color(0xFFF44336)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToNewMeal: () -> Unit = {},
    onNavigateToCreatePatient: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToHealthCenters: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Recargar datos cada vez que el usuario llega a esta pantalla
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    // Mostrar mensaje de error si ocurre
    LaunchedEffect(uiState.confirmDoseError) {
        if (uiState.confirmDoseError != null) {
            Toast.makeText(
                context,
                uiState.confirmDoseError,
                Toast.LENGTH_LONG
            ).show()
            viewModel.clearConfirmDoseError()
        }
    }

    // Mostrar mensaje de éxito cuando se confirma la dosis
    LaunchedEffect(uiState.confirmDoseSuccess) {
        if (uiState.confirmDoseSuccess) {
            Toast.makeText(
                context,
                "¡Dosis confirmada! +10 puntos de adherencia",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "FerovaFamily",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Crimson)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ── Saludo ──
            Text(
                text = "¡Hola ${uiState.userName}!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Crimson
            )
            Text(
                text = "Juntos por la salud de tus pequeños",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.height(20.dp))

            // ── Mis Niños ──
            SectionTitle("Mis Niños")
            Spacer(Modifier.height(8.dp))
            ChildrenRow(
                children = uiState.children,
                onChildSelected = { childId -> viewModel.selectChild(childId) },
                onAddChild = onNavigateToCreatePatient
            )

            Spacer(Modifier.height(16.dp))

            // ── Dosis de hoy ──
            DoseCard(
                onViewHistory = onNavigateToHistory,
                todayDose = uiState.todayDose,
                isConfirmingDose = uiState.isConfirmingDose,
                onConfirmDose = {
                    val selectedChild = uiState.children.find { it.isSelected }
                    selectedChild?.let {
                        viewModel.confirmDose(it.id)
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // ── Logro / Nutrición ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AchievementMiniCard(modifier = Modifier.weight(1f))
                NutritionMiniCard(modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // ── Accesos Rápidos ──
            SectionTitle("Accesos Rapidos")
            Spacer(Modifier.height(12.dp))

            QuickAccessCard(
                icon = Icons.Default.Restaurant,
                title = "Nueva entrada de alimento",
                subtitle = "Registra lo que comiste hoy",
                enabled = false,
                onClick = onNavigateToNewMeal
            )
            Spacer(Modifier.height(10.dp))
            QuickAccessCard(
                icon = Icons.Default.LocationOn,
                title = "Ver Postas Cercanas",
                subtitle = "Encuentra tu centro de salud",
                enabled = true,
                onClick = onNavigateToHealthCenters
            )
            Spacer(Modifier.height(10.dp))
            QuickAccessCard(
                icon = Icons.Default.EmojiEvents,
                title = "Mis Logros y Medallas",
                subtitle = "Revisa tus insignias y recompensas obtenidas",
                enabled = true,
                onClick = onNavigateToAchievements
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Componentes internos ───────────────────────────────

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Crimson
    )
}

@Composable
private fun ChildrenRow(
    children: List<ChildInfo>,
    onChildSelected: (String) -> Unit,
    onAddChild: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Cream),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftPink)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (children.isEmpty()) {
                Text(
                    text = "Aún no tienes hijos registrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            } else {
                children.forEach { child ->
                    ChildAvatar(
                        name = child.name,
                        isSelected = child.isSelected,
                        onClick = { onChildSelected(child.id) }
                    )
                }
            }
            AddChildButton(onClick = onAddChild)
        }
    }
}

@Composable
private fun ChildAvatar(name: String, isSelected: Boolean, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(SoftPink, CircleShape)
                .then(
                    if (isSelected)
                        Modifier.border(2.dp, Crimson, CircleShape)
                    else Modifier
                ),
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
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Crimson else Color.DarkGray
        )
    }
}

@Composable
private fun AddChildButton(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .border(
                    width = 2.dp,
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Agregar",
                tint = Color.Gray
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(text = " ", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DoseCard(
    onViewHistory: () -> Unit,
    todayDose: TodayDose?,
    isConfirmingDose: Boolean,
    onConfirmDose: () -> Unit
) {
    val canConfirm = todayDose?.canConfirm == true
    val scheduledTime = todayDose?.scheduledTime ?: "08:00 AM"
    val isConfirmed = todayDose?.confirmedAt != null
    val hasTreatment = todayDose != null

    // Obtener fecha actual formateada
    val currentDate = java.time.LocalDate.now()
    val formattedDate = currentDate.format(
        java.time.format.DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", java.util.Locale("es"))
    ).replaceFirstChar { it.uppercase() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Cream),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftPink)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dosis de hoy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    TextButton(
                        onClick = onViewHistory,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = "Ver Historial",
                            style = MaterialTheme.typography.labelLarge,
                            color = Crimson,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(SoftPink, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Crimson,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Mostrar fecha actual
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(12.dp))

            // Mostrar horario programado (solo si tiene tratamiento)
            if (hasTreatment) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "",
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Horario programado:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = scheduledTime,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Crimson
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            // Estado de la dosis de hoy
            when {
                // Caso 1: Dosis ya confirmada hoy
                isConfirmed -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "",
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "¡Dosis confirmada hoy!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                // Caso 2: NO tiene tratamiento activo
                !hasTreatment -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "",
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Visita tu posta para que la enfermera active tu tratamiento",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF856404)
                            )
                        }
                    }
                }
                // Caso 3: Tiene tratamiento pero dosis pendiente
                canConfirm -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Crimson.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "",
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Dosis pendiente para hoy",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Crimson,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                // Caso 4: Tiene tratamiento pero no puede confirmar
                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CancelRed.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "",
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "No hay dosis pendiente para hoy",
                                style = MaterialTheme.typography.bodySmall,
                                color = CancelRed
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botón de confirmación
            Button(
                onClick = onConfirmDose,
                modifier = Modifier.fillMaxWidth(),
                enabled = canConfirm && !isConfirmingDose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Crimson,
                    contentColor = Color.White,
                    disabledContainerColor = DisabledGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isConfirmingDose) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "",
                        fontSize = 18.sp
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isConfirmingDose) "Confirmando..." else "Confirmar Dosis",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
@Composable
private fun AchievementMiniCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Logro",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
        Spacer(Modifier.height(6.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Cream),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SoftPink)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Racha Actual: 5 dias",
                    style = MaterialTheme.typography.bodySmall,
                    color = Crimson,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Puntos: 50",
                        style = MaterialTheme.typography.bodySmall,
                        color = Crimson,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Crimson,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NutritionMiniCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Nutricion",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
        Spacer(Modifier.height(6.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Cream),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SoftPink)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Hierro Absorvido Hoy",
                    style = MaterialTheme.typography.bodySmall,
                    color = Crimson,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "1.36",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "mg",
                        style = MaterialTheme.typography.titleMedium,
                        color = Crimson,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickAccessCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (enabled) SoftPink else Color(0xFFE5E5E5)
    val iconBg = if (enabled) Crimson else Color.Gray

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            disabledContainerColor = containerColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}