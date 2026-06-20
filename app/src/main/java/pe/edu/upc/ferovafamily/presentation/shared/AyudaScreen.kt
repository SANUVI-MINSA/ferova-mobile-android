package pe.edu.upc.ferovafamily.presentation.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Help
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
fun AyudaScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Ayuda", fontWeight = FontWeight.Bold) },
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
                imageVector = Icons.Default.Help,
                contentDescription = null,
                tint = Crimson,
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Centro de Ayuda",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Crimson,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            AyudaItem(
                titulo = "¿Cómo inicio sesión?",
                descripcion = "Ingresá tu DNI de 8 dígitos y tu contraseña. Si es tu primera vez, registrate con el botón \"Regístrate\"."
            )
            AyudaItem(
                titulo = "¿Olvidé mi contraseña",
                descripcion = "Tocá \"¿Olvidaste la contraseña?\" en la pantalla de inicio de sesión y seguí los pasos para restablecerla por correo electrónico."
            )
            AyudaItem(
                titulo = "¿Cómo registro a mi hijo/a?",
                descripcion = "Desde el menú principal, accedé a \"Gestión de Pacientes\" y completá el formulario con los datos de tu hijo/a."
            )
            AyudaItem(
                titulo = "¿Cómo contacto a la enfermera?",
                descripcion = "Accedé a la sección \"Consultas\" y seleccioná a tu hijo/a para iniciar una nueva consulta con la enfermera asignada."
            )
            AyudaItem(
                titulo = "¿Cómo registro las dosis de hierro?",
                descripcion = "Desde la pantalla principal, usá el botón de seguimiento de tratamiento para registrar cada dosis diaria."
            )
            AyudaItem(
                titulo = "Contacto y soporte",
                descripcion = "Si tenés problemas técnicos, escribinos a soporte@ferovafamily.pe o llamá al 0800-FEROVA (lunes a viernes, 8am–6pm)."
            )
        }
    }
}

@Composable
private fun AyudaItem(titulo: String, descripcion: String) {
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
