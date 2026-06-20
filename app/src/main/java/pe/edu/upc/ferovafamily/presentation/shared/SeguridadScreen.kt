package pe.edu.upc.ferovafamily.presentation.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
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
fun SeguridadScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Seguridad", fontWeight = FontWeight.Bold) },
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
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = Crimson,
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Seguridad de tu Cuenta",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Crimson,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            SeguridadItem(
                titulo = "Autenticación segura",
                descripcion = "Usamos tokens JWT con expiración automática para proteger tu sesión. Tu contraseña nunca se almacena en texto plano."
            )
            SeguridadItem(
                titulo = "Contraseñas seguras",
                descripcion = "Recomendamos usar contraseñas de al menos 8 caracteres combinando letras, números y símbolos. No compartas tu contraseña con nadie."
            )
            SeguridadItem(
                titulo = "Comunicación cifrada",
                descripcion = "Toda la comunicación entre la aplicación y nuestros servidores se realiza mediante HTTPS con cifrado TLS, protegiendo tus datos en tránsito."
            )
            SeguridadItem(
                titulo = "Cierre de sesión automático",
                descripcion = "Por tu seguridad, la sesión se cierra automáticamente tras un período de inactividad. Siempre podés cerrar sesión manualmente desde el menú principal."
            )
            SeguridadItem(
                titulo = "¿Sospechás de actividad no autorizada?",
                descripcion = "Si creés que alguien accedió a tu cuenta sin permiso, cambiá tu contraseña inmediatamente y contactá a nuestro equipo de soporte."
            )
            SeguridadItem(
                titulo = "Actualizaciones de seguridad",
                descripcion = "Mantenemos la aplicación actualizada con los últimos parches de seguridad. Te recomendamos siempre usar la versión más reciente."
            )
        }
    }
}

@Composable
private fun SeguridadItem(titulo: String, descripcion: String) {
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
