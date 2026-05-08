package pe.edu.upc.ferovafamily.presentation.progress.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import pe.edu.upc.ferovafamily.presentation.progress.ProgressViewModel

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)
private val SoftPink = Color(0xFFF9E8E8)
private val Gold = Color(0xFFFFC107)

@Composable
fun MedalUnlockedScreen(
    medalId: String,
    onBackToHome: () -> Unit,
    onSeeMedals: () -> Unit,
    viewModel: ProgressViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val medal = state.medals.firstOrNull { it.id == medalId }

    Surface(modifier = Modifier.fillMaxSize(), color = Cream) {
        if (medal == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Medalla no encontrada", color = Color.Gray)
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
            // Estrella decorativa
            Icon(Icons.Default.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(28.dp))

            Spacer(Modifier.height(8.dp))

            // Medalla grande
            Box(
                modifier = Modifier.size(140.dp).background(Gold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    medal.type.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Surface(color = SoftPink, shape = RoundedCornerShape(20.dp)) {
                Text(
                    "Logro Desbloqueado",
                    color = Crimson,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                medal.type.celebrationLabel,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Tratamiento de ${medal.targetDays} días",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "¡Mision Cumplida!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Crimson
            )
            Spacer(Modifier.height(8.dp))
            Text(
                medal.celebrationMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Crimson),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Volver al inicio", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onSeeMedals,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ver mis medallas", color = Crimson, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}