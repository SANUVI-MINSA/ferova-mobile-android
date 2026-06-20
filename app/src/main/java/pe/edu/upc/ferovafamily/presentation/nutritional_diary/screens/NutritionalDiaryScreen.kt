package pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.NutritionalDiaryViewModel
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.ActionButtons
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.IronAbsorptionCard
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.TipCard
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.TodayFoodEntriesList
import pe.edu.upc.ferovafamily.presentation.theme.Crimson
import pe.edu.upc.ferovafamily.presentation.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionalDiaryScreen(
    patientId: String,
    onNewFoodEntry: () -> Unit = {},
    onSeeFoodHistory: (String) -> Unit = {},
    viewModel: NutritionalDiaryViewModel = viewModel()
) {
    // ════════════════════════════════════════════════════════════════════════
    // ESTADOS DEL VIEWMODEL
    // ════════════════════════════════════════════════════════════════════════

    val uiState by viewModel.uiState.collectAsState()
    val todayDiary by viewModel.todayDiary.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // ════════════════════════════════════════════════════════════════════════
    // ESTADOS LOCALES
    // ════════════════════════════════════════════════════════════════════════

    // El niño seleccionado proviene del estado del ViewModel (compartido).
    // Si aún no hay selección, usamos el patientId recibido por parámetro.
    val selectedPatientId = uiState.selectedPatient?.id ?: patientId
    val selectedPatientName = uiState.selectedPatient?.name

    // ════════════════════════════════════════════════════════════════════════
    // EFECTOS
    // ════════════════════════════════════════════════════════════════════════

    // Recargar el diario cada vez que cambia el niño seleccionado
    LaunchedEffect(selectedPatientId) {
        if (selectedPatientId.isNotBlank()) {
            android.util.Log.d("NUTRITION_DEBUG", "patientId seleccionado = $selectedPatientId")
            viewModel.loadTodayDiary(selectedPatientId)
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // DATOS DEL DIARIO
    // ════════════════════════════════════════════════════════════════════════

    val totalIronAbsorbed = todayDiary?.totalIronAbsorbed ?: 0.0
    val foodEntries = todayDiary?.foodEntries ?: emptyList()
    val diaryDate = todayDiary?.date ?: ""

    Scaffold(
        containerColor = White,
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = "Diario Nutricional",
                        color = Crimson,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF9F5F3))
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(24.dp))

                // ════════════════════════════════════════════════════════════
                // TARJETA DE ABSORCIÓN DE HIERRO
                // ════════════════════════════════════════════════════════════

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
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
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = " ${error ?: "Error al cargar"}",
                                color = Color(0xFFB71C1C),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    else -> {
                        IronAbsorptionCard(
                            selectedPatient = uiState.selectedPatient,
                            totalIron = totalIronAbsorbed,
                            patients = uiState.patients,
                            onPatientSelected = { patient ->
                                viewModel.selectPatient(patient.id)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ════════════════════════════════════════════════════════════
                // LISTA DE ALIMENTOS DE HOY
                // ════════════════════════════════════════════════════════════

                TodayFoodEntriesList(
                    patientId = selectedPatientId,
                    patientName = selectedPatientName,
                    viewModel = viewModel
                )

                Spacer(Modifier.height(24.dp))

                // ════════════════════════════════════════════════════════════
                // BOTONES DE ACCIÓN
                // ════════════════════════════════════════════════════════════

                ActionButtons(
                    onNewFoodEntry = onNewFoodEntry,
                    onSeeFoodHistory = {
                        onSeeFoodHistory(selectedPatientId)
                    }
                )

                Spacer(Modifier.height(24.dp))

                // ════════════════════════════════════════════════════════════
                // TIPS EDUCATIVOS
                // ════════════════════════════════════════════════════════════

                TipCard(
                    titleTipText = "💡 Tip de hoy",
                    tipText = "Combina las lentejas con cítricos (naranja, limón) para mejorar la absorción del hierro. La Vitamina C aumenta la absorción del hierro no-hemo hasta 3 veces.",
                    titleTipStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    ),
                    tipTextStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF555555),
                        lineHeight = 20.sp
                    ),
                )

                Spacer(Modifier.height(12.dp))

                TipCard(
                    titleTipText = "⚠️ Evita estos alimentos",
                    tipText = "Evita consumir té, café y lácteos durante las comidas principales, ya que reducen la absorción del hierro hasta 50%.",
                    containerColor = Color(0xFFFFF3E0),
                    bulbColor = Color(0xFFFF9800),
                    titleTipStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    ),
                    tipTextStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF555555),
                        lineHeight = 20.sp
                    ),
                )

                Spacer(Modifier.height(12.dp))

                TipCard(
                    titleTipText = "🥇 Alimentos con más hierro",
                    tipText = "La sangrecita de pollo tiene 29.5 mg de hierro por 100g, siendo el alimento con mayor contenido de hierro hemo. ¡Perfecto para fortalecer a tu pequeño!",
                    containerColor = Color(0xFFE8F5E9),
                    bulbColor = Color(0xFF4CAF50),
                    titleTipStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    ),
                    tipTextStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF555555),
                        lineHeight = 20.sp
                    ),
                )

                Spacer(Modifier.height(24.dp))
            }

            // ════════════════════════════════════════════════════════════════
            // LOADER GLOBAL
            // ════════════════════════════════════════════════════════════════

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF8B0000)
                    )
                }
            }
        }
    }
}
