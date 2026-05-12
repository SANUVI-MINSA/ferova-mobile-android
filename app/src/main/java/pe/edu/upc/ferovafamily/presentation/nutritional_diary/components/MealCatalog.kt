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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodItem

@Composable
fun MealCatalog(
    foodItems: List<FoodItem>,
    modifier: Modifier = Modifier
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

    val filteredItems = foodItems.filter { it.category == selectedCategory }

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

        // Lista de food items
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            filteredItems.forEach { foodItem ->
                FoodItemCard(foodItem = foodItem)
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
    onClickCard: () -> Unit = {}
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClickCard),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = foodItem.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B0000),
                modifier = Modifier.weight(1f)
            )

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
                        text = foodItem.nutrientContent.second,
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${foodItem.nutrientContent.first}.0 mg",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1A1A1A)
                )
            }
        }
    }
}