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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import pe.edu.upc.ferovafamily.presentation.theme.Success

@Composable
fun NewPasswordScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val isConfirmPasswordVisible = remember { mutableStateOf(false) }

    val hasMinLength = password.value.length >= 8
    val hasNumber = password.value.any { it.isDigit() }
    val hasSpecial = password.value.any { !it.isLetterOrDigit() }
    val passwordsMatch = password.value == confirmPassword.value
            && confirmPassword.value.isNotEmpty()

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
                text = "Nueva Contraseña",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Crea una contraseña segura para\nproteger tu cuenta",
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
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Nueva Contraseña
            Column {
                Text(
                    text = "Nueva Contraseña",
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
                        focusedBorderColor = if (passwordsMatch) Success else Crimson,
                        unfocusedBorderColor = if (passwordsMatch) Success else RoseBorder,
                        focusedContainerColor = RoseInput,
                        unfocusedContainerColor = RoseInput,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (passwordsMatch)
                                Success
                            else
                                Crimson.copy(alpha = 0.7f)
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

            // Requisitos de seguridad
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Rose)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Requisitos de seguridad",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMid
                )
                SecurityRequirement(
                    text = "Mínimo 8 caracteres",
                    isMet = hasMinLength
                )
                SecurityRequirement(
                    text = "Al menos un número",
                    isMet = hasNumber
                )
                SecurityRequirement(
                    text = "Un carácter especial (@, #, \$)",
                    isMet = hasSpecial
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botón Actualizar Contraseña
            Button(
                onClick = { onNavigateToLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonDark,
                    disabledContainerColor = RoseBorder
                ),
                enabled = hasMinLength && hasNumber
                        && hasSpecial && passwordsMatch
            ) {
                Text(
                    text = "Actualizar Contraseña",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Spacer(modifier = Modifier.size(8.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun SecurityRequirement(
    text: String,
    isMet: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (isMet) Success else RoseBorder)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isMet) Success else TextLight,
            fontWeight = if (isMet) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NewPasswordScreenPreview() {
    FerovaFamilyTheme {
        NewPasswordScreen()
    }
}