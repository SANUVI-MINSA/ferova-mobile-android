package pe.edu.upc.ferovafamily.presentation.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.data.local.TokenManager
import pe.edu.upc.ferovafamily.presentation.auth.AuthResult
import pe.edu.upc.ferovafamily.presentation.auth.AuthViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun VerificationScreen(
    onNavigateToNewPassword: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val code = remember { mutableStateOf("") }
    val context = LocalContext.current
    val tokenManager = remember { TokenManager.getInstance(context) }

    LaunchedEffect(uiState.verifyCodeResult) {
        if (uiState.verifyCodeResult is AuthResult.Success) {
            // Guardar el código temporalmente para la siguiente pantalla
            tokenManager.recoveryCode = code.value
            onNavigateToNewPassword()
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
                text = "Verificar tu identidad",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Hemos enviado un código de 4 dígitos\na tu correo",
                fontSize = 13.sp,
                color = White.copy(alpha = 0.75f),
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                text = "Ingresa el código de verificación",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            // ── 4 cajas de código ────────────────────
            BasicTextField(
                value = code.value,
                onValueChange = { if (it.length <= 4) code.value = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                decorationBox = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            16.dp,
                            Alignment.CenterHorizontally
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(4) { index ->
                            val char = code.value.getOrNull(index)
                            val isFocused = index == code.value.length

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(RoseInput)
                                    .border(
                                        width = if (isFocused) 2.dp else 1.dp,
                                        color = if (isFocused) Crimson else RoseBorder,
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (char != null) "●" else "",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Crimson,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            )

            if (uiState.verifyCodeResult is AuthResult.Error) {
                Text(
                    text = (uiState.verifyCodeResult as AuthResult.Error).message,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontSize = 13.sp
                )
            }

            // Botón Verificar Código
            Button(
                onClick = {
                    val email = tokenManager.recoveryEmail ?: ""
                    viewModel.verifyPasswordCode(email, code.value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonDark,
                    disabledContainerColor = RoseBorder
                ),
                enabled = code.value.length == 4 && uiState.verifyCodeResult !is AuthResult.Loading
            ) {
                Text(
                    text = "Verificar Código",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            // ¿No recibiste el código?
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿No recibiste el código? ",
                    fontSize = 13.sp,
                    color = TextMid
                )
                Text(
                    text = "Reenviar",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Crimson,
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerificationScreenPreview() {
    FerovaFamilyTheme {
        VerificationScreen()
    }
}