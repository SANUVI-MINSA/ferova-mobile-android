package pe.edu.upc.ferovafamily.presentation.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacidadScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Privacidad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Crimson,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PrivacyTip,
                contentDescription = null,
                tint = Crimson,
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Política de Privacidad",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Crimson,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Última actualización: junio 2026",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            PrivacidadItem(
                titulo = "¿Qué datos recopilamos?",
                descripcion = "Recopilamos datos de identificación (DNI, nombre, correo), información de salud de los pacientes registrados (hemoglobina, dosis, citas) y datos de uso de la aplicación para mejorar el servicio."
            )
            PrivacidadItem(
                titulo = "¿Para qué usamos tus datos?",
                descripcion = "Tus datos se utilizan exclusivamente para brindar el servicio de seguimiento nutricional y de salud, facilitar la comunicación con enfermeras asignadas y enviarte notificaciones relacionadas con el tratamiento."
            )
            PrivacidadItem(
                titulo = "¿Compartimos tus datos?",
                descripcion = "No vendemos ni compartimos tus datos con terceros con fines comerciales. Solo los profesionales de salud asignados a tu caso tienen acceso a la información médica de tu hijo/a."
            )
            PrivacidadItem(
                titulo = "Almacenamiento de datos",
                descripcion = "Tu información se almacena en servidores seguros ubicados en Perú, cumpliendo con la Ley N.° 29733 de Protección de Datos Personales y su reglamento."
            )
            PrivacidadItem(
                titulo = "Tus derechos",
                descripcion = "Tenés derecho a acceder, rectificar, cancelar u oponerte al tratamiento de tus datos personales. Para ejercer estos derechos, escribinos a privacidad@ferovafamily.pe."
            )
            PrivacidadItem(
                titulo = "Cambios en esta política",
                descripcion = "Podemos actualizar esta política en cualquier momento. Te notificaremos dentro de la aplicación cuando realicemos cambios significativos."
            )
        }
    }
}

@Composable
private fun PrivacidadItem(titulo: String, descripcion: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = titulo, fontWeight = FontWeight.SemiBold, color = Crimson, fontSize = 15.sp)
            Text(text = descripcion, color = Color.DarkGray, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}
