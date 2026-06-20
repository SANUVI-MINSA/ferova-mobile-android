package pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.NutritionalDiaryViewModel
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.MealEntryHistory
import pe.edu.upc.ferovafamily.presentation.theme.CrimsonDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionalHistoryScreen(
    patientId: String,
    onBack: () -> Unit = {},
    viewModel: NutritionalDiaryViewModel = viewModel()
) {
    // ════════════════════════════════════════════════════════════════════════
    // ESTADOS DEL VIEWMODEL
    // ════════════════════════════════════════════════════════════════════════

    val nutritionalHistory by viewModel.nutritionalHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // ════════════════════════════════════════════════════════════════════════
    // EFECTOS
    // ════════════════════════════════════════════════════════════════════════

    // Cargar historial nutricional cuando se monta la pantalla
    LaunchedEffect(patientId) {
        viewModel.loadNutritionalHistory(patientId)
    }

    Scaffold(
        containerColor = Color(0xFFFFF8F6),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Historial Nutricional",
                        color = CrimsonDark,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Volver",
                            tint = CrimsonDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF8F6))
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // ════════════════════════════════════════════════════════════════
            // MOSTRAR CONTENIDO O ESTADO
            // ════════════════════════════════════════════════════════════════

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF8B0000)
                        )
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = " ${error ?: "Error al cargar historial"}",
                            color = Color(0xFFB71C1C),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                else -> {
                    // ════════════════════════════════════════════════════════
                    // COMPONENTE DE HISTORIAL
                    // ════════════════════════════════════════════════════════

                    MealEntryHistory(
                        patientId = patientId,
                        viewModel = viewModel,
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
