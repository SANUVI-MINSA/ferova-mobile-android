package pe.edu.upc.ferovafamily.presentation.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.presentation.auth.AuthResult
import pe.edu.upc.ferovafamily.presentation.auth.AuthViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.presentation.theme.Crimson
import pe.edu.upc.ferovafamily.presentation.theme.CrimsonDark
import pe.edu.upc.ferovafamily.presentation.theme.Cream
import pe.edu.upc.ferovafamily.presentation.theme.FerovaFamilyTheme
import pe.edu.upc.ferovafamily.presentation.theme.RoseBorder
import pe.edu.upc.ferovafamily.presentation.theme.RoseInput
import pe.edu.upc.ferovafamily.presentation.theme.TextDark
import pe.edu.upc.ferovafamily.presentation.theme.TextLight
import pe.edu.upc.ferovafamily.presentation.theme.TextMid
import pe.edu.upc.ferovafamily.presentation.theme.White

@Composable
fun RecoveryPasswordScreen(
    onNavigateToVerification: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val email = remember { mutableStateOf("") }

    LaunchedEffect(uiState.requestCodeResult) {
        if (uiState.requestCodeResult is AuthResult.Success) {
            onNavigateToVerification()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Crimson),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Header rojo ──────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 52.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón volver
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = White
                    )
                }
                Text(
                    text = "Volver",
                    fontSize = 14.sp,
                    color = White,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_ferova),
                    contentDescription = "Logo Ferova",
                    modifier = Modifier
                        .size(62.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¿Olvidaste tu Contraseña?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ingresa tu correo para recibir un\ncódigo de recuperación",
                fontSize = 13.sp,
                color = White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Solo madres pueden recuperar su contraseña",
                fontSize = 11.sp,
                color = White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

        }

        // ── Card blanca ──────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Cream)
                .padding(horizontal = 24.dp, vertical = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Correo Electrónico",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMid,
                letterSpacing = 0.3.sp
            )

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = {
                    Text(
                        "ejemplo@correo.com",
                        color = TextLight,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Crimson,
                    unfocusedBorderColor = RoseBorder,
                    focusedContainerColor = RoseInput,
                    unfocusedContainerColor = RoseInput,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Crimson.copy(alpha = 0.7f)
                    )
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Error
            if (uiState.requestCodeResult is AuthResult.Error) {
                Text(
                    text = (uiState.requestCodeResult as AuthResult.Error).message,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontSize = 13.sp
                )
            }

            // Botón Enviar Código
            Button(
                onClick = { viewModel.requestPasswordCode(email.value) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonDark
                ),
                enabled = email.value.isNotEmpty() && uiState.requestCodeResult !is AuthResult.Loading
            ) {
                Text(
                    text = "Enviar Código  →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Volver al inicio de sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Crimson,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "Volver al Inicio de Sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Crimson,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ferova protege tus datos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = TextLight,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = "Ferova protege tus datos personales",
                    fontSize = 12.sp,
                    color = TextLight
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecoveryPasswordScreenPreview() {
    FerovaFamilyTheme {
        RecoveryPasswordScreen()
    }
}