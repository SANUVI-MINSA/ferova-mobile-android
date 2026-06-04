package pe.edu.upc.ferovafamily.presentation.patient_management.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.presentation.patient_management.PatientResult
import pe.edu.upc.ferovafamily.presentation.patient_management.PatientViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.presentation.patient_management.components.ChildCreationForm
import pe.edu.upc.ferovafamily.presentation.theme.Cream
import pe.edu.upc.ferovafamily.presentation.theme.Crimson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePatientScreen(
    onRegisterChild: () -> Unit = {},
    onBack: () -> Unit = {},
    patientViewModel: PatientViewModel = viewModel()
) {
    val uiState by patientViewModel.uiState.collectAsState()
    val childData = remember { mutableStateOf(ChildData()) }

    // Navegar al éxito
    LaunchedEffect(uiState.registerResult) {
        if (uiState.registerResult is PatientResult.Success) {
            patientViewModel.resetResult()
            onRegisterChild()
        }
    }

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
                .padding(padding)
                .verticalScroll(state = rememberScrollState()),
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

            if (uiState.registerResult is PatientResult.Error) {
                androidx.compose.material3.Text(
                    text = (uiState.registerResult as PatientResult.Error).message,
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            ChildCreationForm(onRegister = { data ->
                childData.value = data
                val genderApi = if (data.gender == "Masculino") "MALE" else "FEMALE"

                // Convertir fecha de MM/dd/yyyy → yyyy-MM-dd (formato ISO exigido por el backend)
                val isoDate = convertToIsoDate(data.birthDate)

                // Si el usuario ingresó metros (ej: 1.72) → convertir a cm (172.0)
                val heightCm = if (data.height in 0.5..3.0) data.height * 100 else data.height

                patientViewModel.registerPatient(
                    name      = data.name,
                    lastName  = data.lastName,
                    birthDate = isoDate,
                    gender    = genderApi,
                    weight    = data.weight,
                    height    = heightCm
                )
            })

            Spacer(Modifier.height(24.dp))
            Text(
                "Al registrar, aceptas que FerovaFamilia guarde los datos de salud para el seguimiento del tratamiento",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

data class ChildData(
    val name: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val weight: Double = 0.0,
    val height: Double = 0.0
)

/** Convierte MM/dd/yyyy → yyyy-MM-dd que exige el backend */
fun convertToIsoDate(date: String): String {
    return try {
        val parts = date.split("/")
        if (parts.size == 3) {
            val month = parts[0].padStart(2, '0')
            val day   = parts[1].padStart(2, '0')
            val year  = parts[2]
            "$year-$month-$day"
        } else date
    } catch (_: Exception) { date }
}

@Preview
@Composable
fun CreatePatientPreview() {
    CreatePatientScreen()
}