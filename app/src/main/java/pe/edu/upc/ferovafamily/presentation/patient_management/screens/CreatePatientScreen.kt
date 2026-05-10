package pe.edu.upc.ferovafamily.presentation.patient_management.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.presentation.theme.White
import java.util.*

private val Crimson = Color(0xFF8B1A1A)
private val Cream = Color(0xFFFDF8F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePatientScreen(
    onRegisterChild: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = { Text("Registro del paciente", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Crimson,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            AsyncImage(model = R.drawable.mother_and_son, "imagen")
            Spacer(Modifier.height(24.dp))

            Text("¡Bienvenido/a!", fontWeight = FontWeight.Bold, fontSize = 32.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "Comencemos el camino hacia una vida llena de vitalidad para tu pequeño(a)",
                modifier = Modifier.padding(horizontal = 40.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light
            )
            Spacer(Modifier.height(16.dp))

            ChildCreationForm(onRegisterChild)

            Spacer(Modifier.height(24.dp))
            Text(
                "Al registrar, aceptas que FerovaFamilia guarde los datos de salud para el seguimiento del tratamiento",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun ChildCreationForm(
    onClick: () -> Unit
) {
    val name = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val birthDate = remember { mutableStateOf("") }
    val gender = remember { mutableStateOf("male") }
    val weight = remember { mutableDoubleStateOf(0.0) }
    val height = remember { mutableIntStateOf(0) }

    val isSelected = gender.value == "male"

    Surface() { }
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(8.dp),   // esquinas redondeadas
            colors = CardDefaults.cardColors(
                containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // sombra suave
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)) // borde gris claro
        ) {
            Column(
                modifier = Modifier
                .padding(horizontal = 24.dp)  // 👈 espacio DENTRO del card (padding)
                .fillMaxWidth())
            {
                Spacer(Modifier.height(16.dp))
                Text("Nombres del niño/a")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = name.value, placeholder = {
                    Text("Ej. Diana Lucia", fontWeight = FontWeight.Light, fontSize = 12.sp)
                } ,onValueChange = {
                    name.value = it
                }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(16.dp))
                Text("Apellidos")
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(value = lastName.value, placeholder = {
                    Text("Ej. Briceño Vera", fontWeight = FontWeight.Light, fontSize = 12.sp)
                }, onValueChange = {
                    lastName.value = it
                }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(16.dp))
                Text("Fecha de nacimiento")
                Spacer(Modifier.height(8.dp))

                DatePickerTextField(
                    value = birthDate.value,
                    onDateSelected = { birthDate.value = it },
                )

                Spacer(Modifier.height(16.dp))

                Row() {

                }
            }

        }
        Spacer(Modifier.height(16.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Crimson,
                contentColor = White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 18.dp),
            onClick = onClick
        ) {
            Text("Registrar a mi pequeño", fontSize = 16.sp, fontWeight = FontWeight.Normal)
            Spacer(Modifier.width(24.dp))
            AsyncImage(
                model = R.drawable.favorite, "heart",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "mm/dd/yyyy",
    dateFormat: String = "MM/dd/yyyy",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(modifier = modifier) {

        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(
                    width = 1.dp,
                    color = when {
                        isError   -> MaterialTheme.colorScheme.error
                        !enabled  -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        else      -> MaterialTheme.colorScheme.outline
                    },
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable(enabled = enabled) { showPicker = true }
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = value.ifEmpty { placeholder },
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 0.5.sp,
                    color = if (value.isEmpty())
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    else
                        MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = "Select date",
                tint = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }

        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
            )
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
                            onDateSelected(sdf.format(Date(millis)))
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

data class ChildData(
    val name: String,
    val lastName: String,
    val birthDate: String,
    val gender: String,
    val weight: Double,
    val height: Int
)

@Preview
@Composable
fun CreatePatientPreview() {
    CreatePatientScreen()
}