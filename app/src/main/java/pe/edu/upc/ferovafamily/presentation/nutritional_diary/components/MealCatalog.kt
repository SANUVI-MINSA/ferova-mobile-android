package pe.edu.upc.ferovafamily.presentation.nutritional_diary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.ferovafamily.domain.model.nutrition.FoodItem
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.NutritionalDiaryViewModel

@Composable
fun MealCatalog(
    modifier: Modifier = Modifier,
    viewModel: NutritionalDiaryViewModel,
    onMealClick: (FoodItem) -> Unit = {},
) {
    val categories = listOf(
        "MEAT" to "Carnes",
        "VEGETABLE" to "Verduras",
        "LEGUME" to "Legumbres",
        "FISH" to "Pescados",
        "DAIRY" to "Lácteos",
        "GRAIN" to "Cereales",
        "FRUIT" to "Frutas",
        "BEVERAGE" to "Bebidas"
    )

    var selectedCategory by remember { mutableStateOf("MEAT") }

    // ════════════════════════════════════════════════════════════════════════
    // ESTADOS DEL VIEWMODEL
    // ════════════════════════════════════════════════════════════════════════

    val foodsByCategory by viewModel.foodsByCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // ════════════════════════════════════════════════════════════════════════
    // EFECTOS
    // ════════════════════════════════════════════════════════════════════════

    // Cargar alimentos cuando cambia la categoría
    LaunchedEffect(selectedCategory) {
        viewModel.loadFoodsByCategory(selectedCategory)
    }

    val filteredItems = foodsByCategory?.items ?: emptyList()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            modifier = Modifier.padding(start = 24.dp),
            text = "Categorías",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        // Fila de categorías scrolleable
        LazyRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { (key, label) ->
                CategoryChip(
                    label = label,
                    isSelected = selectedCategory == key,
                    onClick = { selectedCategory = key }
                )
            }
        }

        // ════════════════════════════════════════════════════════════════════
        // MOSTRAR ESTADO
        // ════════════════════════════════════════════════════════════════════

        when {
            isLoading -> {
                // Mostrar loader
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
                // Mostrar error
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = " ${error ?: "Error desconocido"}",
                        fontSize = 14.sp,
                        color = Color(0xFFB71C1C),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            filteredItems.isEmpty() -> {
                // Sin resultados
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin alimentos disponibles",
                        fontSize = 14.sp,
                        color = Color(0xFF888888)
                    )
                }
            }

            else -> {
                // Lista de food items
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    filteredItems.forEach { foodItem ->
                        FoodItemCard(
                            foodItem = foodItem,
                            onClickCard = onMealClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) Color(0xFF8B0000) else Color.White)
            .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color(0xFF888888)
        )
    }
}

@Composable
fun FoodItemCard(
    foodItem: FoodItem,
    modifier: Modifier = Modifier,
    onClickCard: (FoodItem) -> Unit = {}
) {
    // ════════════════════════════════════════════════════════════════════════
    // COLOR DE ALERTA SI ES INHIBIDOR
    // ════════════════════════════════════════════════════════════════════════

    val cardBgColor = if (foodItem.isInhibitor) Color(0xFFFFF3E0) else Color.White
    val borderColor = if (foodItem.isInhibitor) Color(0xFFFF9800) else Color.White

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickCard(foodItem) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (foodItem.isInhibitor)
            CardDefaults.outlinedCardBorder()
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = foodItem.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B0000)
                )

                // ════════════════════════════════════════════════════════════
                // ADVERTENCIA SI ES INHIBIDOR
                // ════════════════════════════════════════════════════════════

                if (foodItem.isInhibitor) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = " Reduce absorción de hierro",
                        fontSize = 11.sp,
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                // Badge Hemo / No-Hemo
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF8B0000))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (foodItem.ironType == "hemo") "Hemo" else "No-Hemo",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${foodItem.ironMgPer100g} mg",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1A1A1A)
                )
            }
        }
    }
}