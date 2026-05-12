package pe.edu.upc.ferovafamily.presentation.home

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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)
private val SoftPink = Color(0xFFF9E8E8)
private val DisabledGray = Color(0xFFB8B8B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToNewMeal: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
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
                actions = {
                    IconButton(onClick = { /* TODO: notificaciones */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
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
                text = "¡Hola María!",
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
            ChildrenRow(onClick = onNavigateToCreatePatient)

            Spacer(Modifier.height(16.dp))

            // ── Dosis de hoy ──
            DoseCard(onViewHistory = onNavigateToHistory)

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
                enabled = false,
                onClick = { /* TODO */ }
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
    onClick: () -> Unit
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
            ChildAvatar(name = "Mateo", isSelected = true)
            ChildAvatar(name = "Lucia", isSelected = false)
            AddChildButton(onClick = onClick)
        }
    }
}

@Composable
private fun ChildAvatar(name: String, isSelected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }) {
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
private fun DoseCard(onViewHistory: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Cream),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftPink)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("L", "M", "M", "J", "V", "S", "D")
                val completedDays = listOf("L", "M", "M")

                days.forEach { day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    if (day in completedDays) Crimson else DisabledGray,
                                    CircleShape
                                )
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Crimson,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Confirmar Dosis",
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