package pe.edu.upc.ferovafamily.presentation.progress.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upc.ferovafamily.presentation.progress.model.Medal

private val Crimson = Color(0xFF8B1A1A)
private val SoftPink = Color(0xFFF9E8E8)
private val Gold = Color(0xFFFFC107)
private val SuccessGreen = Color(0xFF4CAF50)

@Composable
fun MedalListItem(
    medal: Medal,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftPink)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (medal.isUnlocked) Gold else Color(0xFFE0E0E0),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (medal.isUnlocked) medal.type.icon else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (medal.isUnlocked) Color.White else Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medal.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (medal.isUnlocked) Color.DarkGray else Color.Gray
                )
                Text(
                    text = medal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(Modifier.height(6.dp))

                //  DIFERENTE SEGÚN SI ESTÁ DESBLOQUEADA
                if (medal.isUnlocked) {
                    // Medalla desbloqueada: mostrar check y mensaje
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Crimson,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Completado",
                            style = MaterialTheme.typography.labelSmall,
                            color = Crimson,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "${medal.targetDays}/${medal.targetDays} días",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                } else {
                    // Medalla bloqueada: mostrar barra de progreso
                    LinearProgressIndicator(
                        progress = { medal.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = Crimson,
                        trackColor = SoftPink
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${medal.currentDays}/${medal.targetDays} días",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "${(medal.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Crimson,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}