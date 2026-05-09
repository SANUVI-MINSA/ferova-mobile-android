package pe.edu.upc.ferovafamily.presentation.patient_management.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import pe.edu.upc.ferovafamily.presentation.theme.White

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
            Text("Al registrar, aceptas que FerovaFamilia guarde los datos de salud para el seguimiento del tratamiento",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(horizontal = 32.dp))
        }
    }
}

@Composable
fun ChildCreationForm(
    onClick: () -> Unit
) {
    val name = remember {mutableStateOf("")}
    val lastName = remember {mutableStateOf("")}
    val birthDate = remember { mutableStateOf("") }
    val gender = remember { mutableStateOf("male") }
    val weight = remember {mutableDoubleStateOf(0.0)}
    val height = remember { mutableIntStateOf(0) }

    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(color = White),
        ) {
            Text("Nombres del niño/a")

            Text("Apellidos")
            Text("Fecha de nacimiento")
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
            AsyncImage(model = R.drawable.favorite, "heart",
                modifier = Modifier.size(24.dp))
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