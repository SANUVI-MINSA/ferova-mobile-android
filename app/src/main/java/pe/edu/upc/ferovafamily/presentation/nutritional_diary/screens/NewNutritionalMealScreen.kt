package pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.domain.model.nutrition.FoodItem
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.NutritionalDiaryViewModel
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.FoodItemCard
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.MealCatalog
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.MealSearch
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.RegisterMealDialog
import pe.edu.upc.ferovafamily.presentation.theme.CrimsonDark

private const val TAG = "NewNutritionalMeal"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewNutritionalMealScreen(
    patientId: String,
    onBack: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    viewModel: NutritionalDiaryViewModel = viewModel()
) {
    Log.d(TAG, "========================================")
    Log.d(TAG, "📱 NewNutritionalMealScreen - INICIO")
    Log.d(TAG, "📱 patientId: $patientId")
    Log.d(TAG, "========================================")

    // ════════════════════════════════════════════════════════════════════════
    // ESTADOS DEL VIEWMODEL
    // ════════════════════════════════════════════════════════════════════════

    val searchFoodResult by viewModel.searchFoodResult.collectAsState()
    val foodsByCategory by viewModel.foodsByCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val warning by viewModel.warning.collectAsState()

    // ════════════════════════════════════════════════════════════════════════
    // ESTADOS LOCALES
    // ════════════════════════════════════════════════════════════════════════

    var selectedMeal by remember { mutableStateOf<FoodItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // ════════════════════════════════════════════════════════════════════════
    // EFECTO: CARGAR CATEGORÍA POR DEFECTO AL MONTAR
    // ════════════════════════════════════════════════════════════════════════

    LaunchedEffect(Unit) {
        Log.d(TAG, "🔄 Cargando categoría MEAT por defecto")
        viewModel.loadFoodsByCategory("MEAT")
    }

    // ════════════════════════════════════════════════════════════════════════
    // MANEJADOR DE BÚSQUEDA
    // ════════════════════════════════════════════════════════════════════════

    val handleSearch = { query: String ->
        Log.d(TAG, "🔍 Búsqueda: '$query'")
        searchQuery = query
        if (query.isEmpty()) {
            Log.d(TAG, "📂 Cargando categoría MEAT por defecto")
            viewModel.loadFoodsByCategory("MEAT")
        } else if (query.length >= 2) {
            Log.d(TAG, "🔍 Buscando: '$query'")
            viewModel.searchFoods(query)
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // OBTENER ALIMENTOS SEGÚN BÚSQUEDA O CATEGORÍA
    // ════════════════════════════════════════════════════════════════════════

    val foodItemsToDisplay = if (searchQuery.isEmpty()) {
        foodsByCategory?.items ?: emptyList()
    } else {
        searchFoodResult?.items ?: emptyList()
    }

    Log.d(TAG, "📋 foodItemsToDisplay size: ${foodItemsToDisplay.size}")

    Scaffold(
        containerColor = Color(0xFFFFF8F6),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NutriHierro",
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                Spacer(Modifier.height(16.dp))

                MealSearch(
                    searchQuery = searchQuery,
                    onSearchQueryChange = handleSearch
                )

                Spacer(Modifier.height(16.dp))

                if (searchQuery.isEmpty()) {
                    MealCatalog(
                        viewModel = viewModel,
                        onMealClick = { foodItem ->
                            Log.d(TAG, "👆 Click en alimento: ${foodItem.name} (ID: ${foodItem.foodItemId})")
                            selectedMeal = foodItem
                        }
                    )
                } else {
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
                                    text = " ${error ?: "Error desconocido"}",
                                    color = Color(0xFFB71C1C),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        searchQuery.length < 2 -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Escribe al menos 2 caracteres para buscar",
                                    color = Color(0xFF888888)
                                )
                            }
                        }

                        foodItemsToDisplay.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron alimentos",
                                    color = Color(0xFF888888)
                                )
                            }
                        }

                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            ) {
                                Text(
                                    text = "Resultados (${foodItemsToDisplay.size})",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                foodItemsToDisplay.forEach { foodItem ->
                                    FoodItemCard(
                                        foodItem = foodItem,
                                        onClickCard = {
                                            Log.d(TAG, "👆 Click en resultado: ${it.name} (ID: ${it.foodItemId})")
                                            selectedMeal = it
                                        }
                                    )
                                    Spacer(Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }

            selectedMeal?.let { item ->
                Log.d(TAG, "📦 Abriendo diálogo para: ${item.name} (ID: ${item.foodItemId})")
                LaunchedEffect(item.foodItemId) {
                    viewModel.clearRegisterResult()
                }
                RegisterMealDialog(
                    foodItem = item,
                    patientId = patientId,
                    viewModel = viewModel,
                    onDismiss = {
                        Log.d(TAG, "❌ Dialogo descartado")
                        selectedMeal = null
                        viewModel.clearWarning()
                        viewModel.clearError()
                        viewModel.clearRegisterResult()
                    },
                    onSuccess = {
                        Log.d(TAG, "✅ Registro exitoso, cerrando pantalla")
                        selectedMeal = null
                        onRegisterSuccess()
                    }
                )
            }
        }
    }
}