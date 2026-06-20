package pe.edu.upc.ferovafamily.presentation.auth

import pe.edu.upc.ferovafamily.R
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.presentation.theme.Crimson
import pe.edu.upc.ferovafamily.presentation.theme.CrimsonDark
import pe.edu.upc.ferovafamily.presentation.theme.Cream
import pe.edu.upc.ferovafamily.presentation.theme.FerovaFamilyTheme
import pe.edu.upc.ferovafamily.presentation.theme.Rose
import pe.edu.upc.ferovafamily.presentation.theme.RoseBorder
import pe.edu.upc.ferovafamily.presentation.theme.RoseInput
import pe.edu.upc.ferovafamily.presentation.theme.TextDark
import pe.edu.upc.ferovafamily.presentation.theme.TextLight
import pe.edu.upc.ferovafamily.presentation.theme.TextMid
import pe.edu.upc.ferovafamily.presentation.theme.White

// Optimiza el LoginScreen separando componentes y usando remember correctamente

@Composable
fun LoginScreen(
    onNavigateToCreateAccount: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToRecovery: () -> Unit = {},
    onNavigateToAyuda: () -> Unit = {},
    onNavigateToSeguridad: () -> Unit = {},
    onNavigateToPrivacidad: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // ✅ Usar delegated properties para evitar recomposiciones innecesarias
    var dni by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Navegación cuando el login es exitoso
    LaunchedEffect(uiState.loginResult) {
        if (uiState.loginResult is AuthResult.Success) {
            viewModel.resetLoginResult()
            onNavigateToHome()
        }
    }

    // Animación SOLO al inicio
    var cardVisible by remember { mutableStateOf(false) }
    val cardOffset by animateDpAsState(
        targetValue = if (cardVisible) 0.dp else 60.dp,
        animationSpec = tween(durationMillis = 500),
        label = "cardOffset"
    )
    LaunchedEffect(Unit) {
        cardVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Crimson),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginHeader()

        LoginCard(
            cardOffset = cardOffset,
            dni = dni,
            onDniChange = { if (it.length <= 8) dni = it },
            password = password,
            onPasswordChange = { password = it },
            isPasswordVisible = isPasswordVisible,
            onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
            loginResult = uiState.loginResult,
            isLoading = uiState.loginResult is AuthResult.Loading,
            onLoginClick = { viewModel.login(dni, password) },
            onNavigateToRecovery = onNavigateToRecovery,
            onNavigateToCreateAccount = onNavigateToCreateAccount,
            onNavigateToAyuda = onNavigateToAyuda,
            onNavigateToSeguridad = onNavigateToSeguridad,
            onNavigateToPrivacidad = onNavigateToPrivacidad
        )
    }
}

// ✅ Extraer componentes para mejor rendimiento
@Composable
private fun LoginHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 56.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_ferova),
                    contentDescription = "Logo Ferova",
                    modifier = Modifier
                        .size(70.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Ferova Family",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = White,
            letterSpacing = 0.3.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Bienvenido de nuevo",
            fontSize = 13.sp,
            color = White.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun LoginCard(
    cardOffset: Dp,
    dni: String,
    onDniChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    loginResult: AuthResult,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onNavigateToCreateAccount: () -> Unit,
    onNavigateToAyuda: () -> Unit = {},
    onNavigateToSeguridad: () -> Unit = {},
    onNavigateToPrivacidad: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = cardOffset)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                clip = false
            )
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(Cream)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Iniciar sesión",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Text(
            text = "Ingresa tus credenciales para continuar",
            fontSize = 13.sp,
            color = TextLight
        )

        Spacer(modifier = Modifier.height(4.dp))

        // DNI
        Text(
            text = "DNI (8 dígitos)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextMid
        )
        OutlinedTextField(
            value = dni,
            onValueChange = onDniChange,
            placeholder = { Text("00000000", color = TextLight) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Crimson,
                unfocusedBorderColor = RoseBorder,
                focusedContainerColor = RoseInput,
                unfocusedContainerColor = RoseInput,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = TextLight
                )
            },
            singleLine = true
        )

        // Contraseña (similar, pero con visualTransformation)
        Text(
            text = "Contraseña",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextMid
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text("***********", color = TextLight) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Crimson,
                unfocusedBorderColor = RoseBorder,
                focusedContainerColor = RoseInput,
                unfocusedContainerColor = RoseInput,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark
            ),
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = TextLight
                    )
                }
            },
            singleLine = true
        )

        Text(
            text = "¿Olvidaste la contraseña?",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Crimson,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToRecovery() },
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Mensaje de error
        if (loginResult is AuthResult.Error) {
            Text(
                text = loginResult.message,
                color = androidx.compose.ui.graphics.Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Botón Iniciar Sesión
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            enabled = dni.isNotBlank() && password.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonDark)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 4.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¿No tienes cuenta? ",
                fontSize = 14.sp,
                color = TextMid
            )
            Text(
                text = "Regístrate",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Crimson,
                modifier = Modifier.clickable { onNavigateToCreateAccount() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomIconItem(icon = Icons.Default.Help, label = "Ayuda", onClick = onNavigateToAyuda)
            BottomIconItem(icon = Icons.Default.Security, label = "Seguridad", onClick = onNavigateToSeguridad)
            BottomIconItem(icon = Icons.Default.PrivacyTip, label = "Privacidad", onClick = onNavigateToPrivacidad)
        }
    }
}
@Composable
fun BottomIconItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Rose)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Crimson,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextLight
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    FerovaFamilyTheme {
        LoginScreen()
    }
}