package pe.edu.upc.ferovafamily.presentation.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.ferovafamily.R
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

@Composable
fun CreateAccountScreen(
    onNavigateToLogin: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val dni = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val isConfirmPasswordVisible = remember { mutableStateOf(false) }
    val acceptedTerms = remember { mutableStateOf(false) }

    // Navegar al login cuando el registro es exitoso
    LaunchedEffect(uiState.registerResult) {
        if (uiState.registerResult is AuthResult.Success) {
            viewModel.resetRegisterResult()
            onNavigateToLogin()
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
                .padding(top = 52.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                text = "Crear tu cuenta",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Únete a nuestra comunidad de cuidado integral",
                fontSize = 13.sp,
                color = White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Nombre
            FerovaTextField(
                label = "Nombre completo",
                value = firstName.value,
                onValueChange = { firstName.value = it },
                placeholder = "Ej: Maria Elena",
                icon = Icons.Default.Person
            )

            // Apellido
            FerovaTextField(
                label = "Apellido completo",
                value = lastName.value,
                onValueChange = { lastName.value = it },
                placeholder = "Ej: Garcia Lopez",
                icon = Icons.Default.Person
            )

            // DNI
            FerovaTextField(
                label = "DNI (8 dígitos)",
                value = dni.value,
                onValueChange = { if (it.length <= 8) dni.value = it },
                placeholder = "12345678",
                icon = Icons.Default.Badge
            )

            // Teléfono
            FerovaTextField(
                label = "Teléfono",
                value = phone.value,
                onValueChange = { phone.value = it },
                placeholder = "+51 936 266 002",
                icon = Icons.Default.Phone
            )

            // Correo
            FerovaTextField(
                label = "Correo Electrónico",
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = "ejemplo@correo.com",
                icon = Icons.Default.Email
            )

            // Contraseña
            Column {
                Text(
                    text = "Contraseña",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMid,
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    placeholder = {
                        Text(
                            "*************",
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
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Crimson.copy(alpha = 0.7f)
                        )
                    },
                    visualTransformation = if (isPasswordVisible.value)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            isPasswordVisible.value = !isPasswordVisible.value
                        }) {
                            Icon(
                                imageVector = if (isPasswordVisible.value)
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
            }

            // Confirmar Contraseña
            Column {
                Text(
                    text = "Confirmar Contraseña",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMid,
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                OutlinedTextField(
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it },
                    placeholder = {
                        Text(
                            "*************",
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
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Crimson.copy(alpha = 0.7f)
                        )
                    },
                    visualTransformation = if (isConfirmPasswordVisible.value)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            isConfirmPasswordVisible.value =
                                !isConfirmPasswordVisible.value
                        }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible.value)
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
            }

            // Términos y condiciones
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Rose)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Checkbox(
                    checked = acceptedTerms.value,
                    onCheckedChange = { acceptedTerms.value = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Crimson,
                        uncheckedColor = RoseBorder
                    )
                )
                Text(
                    text = "Acepto términos y condiciones de salud y privacidad",
                    fontSize = 13.sp,
                    color = TextMid,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Mensaje contraseñas no coinciden
            if (confirmPassword.value.isNotEmpty() && password.value != confirmPassword.value) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Mensaje de error del backend
            if (uiState.registerResult is AuthResult.Error) {
                Text(
                    text = (uiState.registerResult as AuthResult.Error).message,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Botón Registrarse
            Button(
                onClick = {
                    if (password.value != confirmPassword.value) return@Button
                    viewModel.registerMother(
                        name = firstName.value,
                        lastname = lastName.value,
                        dni = dni.value,
                        email = email.value,
                        phone = phone.value,
                        password = password.value
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonDark,
                    disabledContainerColor = RoseBorder
                ),
                enabled = acceptedTerms.value
                        && uiState.registerResult !is AuthResult.Loading
                        && (confirmPassword.value.isEmpty() || password.value == confirmPassword.value)
            ) {
                if (uiState.registerResult is AuthResult.Loading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Registrarse  →",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }

            // ¿Ya tienes cuenta?
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    fontSize = 14.sp,
                    color = TextMid
                )
                Text(
                    text = "Iniciar sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Crimson,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateAccountScreenPreview() {
    FerovaFamilyTheme {
        CreateAccountScreen()
    }
}