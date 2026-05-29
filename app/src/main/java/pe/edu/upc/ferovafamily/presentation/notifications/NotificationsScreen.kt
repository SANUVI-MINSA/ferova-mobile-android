package pe.edu.upc.ferovafamily.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)
private val SoftPink = Color(0xFFF9E8E8)

private data class NotificationItem(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val body: String,
    val time: String,
    val isRead: Boolean
)

private val mockNotifications = listOf(
    NotificationItem(
        id = "1",
        icon = Icons.Default.MedicalServices,
        title = "Recordatorio de dosis",
        body = "Es hora de la dosis de hierro de Mateo. No olvides registrarla.",
        time = "Hace 10 min",
        isRead = false
    ),
    NotificationItem(
        id = "2",
        icon = Icons.Default.CalendarMonth,
        title = "Cita confirmada",
        body = "Tu cita en el CS San Juan Miraflores está confirmada para mañana a las 10:00 AM.",
        time = "Hace 1 hora",
        isRead = false
    ),
    NotificationItem(
        id = "3",
        icon = Icons.Default.CheckCircle,
        title = "¡Racha de 5 días!",
        body = "Mateo lleva 5 días seguidos con su tratamiento. ¡Sigue así!",
        time = "Ayer",
        isRead = true
    ),
    NotificationItem(
        id = "4",
        icon = Icons.Default.MedicalServices,
        title = "Recordatorio de dosis",
        body = "Es hora de la dosis de hierro de Lucía.",
        time = "Ayer",
        isRead = true
    ),
    NotificationItem(
        id = "5",
        icon = Icons.Default.CalendarMonth,
        title = "Cita próxima",
        body = "Tienes una cita en el CS Villa María del Triunfo en 2 días.",
        time = "Hace 2 días",
        isRead = true
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notificaciones",
                        fontWeight = FontWeight.Bold
                    )
                },
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
        }
    ) { padding ->
        if (mockNotifications.isEmpty()) {
            EmptyNotifications(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(mockNotifications, key = { it.id }) { notification ->
                    NotificationCard(notification)
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationItem) {
    val bgColor = if (notification.isRead) Color.White else SoftPink

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(if (notification.isRead) Color(0xFFEEEEEE) else Crimson, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    tint = if (notification.isRead) Color.Gray else Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (notification.isRead) Color.DarkGray else Crimson
                    )
                    Text(
                        text = notification.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun EmptyNotifications(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Notifications,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Sin notificaciones",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
        Text(
            text = "Aquí verás recordatorios de dosis y citas",
            style = MaterialTheme.typography.bodySmall,
            color = Color.LightGray
        )
    }
}
