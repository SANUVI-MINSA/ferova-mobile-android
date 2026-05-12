package pe.edu.upc.ferovafamily.presentation.nutritional_diary.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodEntry
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodItem
import pe.edu.upc.ferovafamily.presentation.theme.CrimsonDark
import java.util.Locale

@Composable
fun TodayFoodEntriesList(
    patientName: String,
    date: String,
    foodEntries: List<FoodEntry>,
    foodItems: List<FoodItem>,
    modifier: Modifier = Modifier
) {
    val todayEntries = foodEntries.filter {
        it.patientName == patientName && it.registeredAt == date
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Alimentos de Hoy",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B0000)
        )

        todayEntries.forEach { entry ->
            val foodItem = foodItems.find { it.id == entry.foodItemId }
            val isInhibitor = foodItem?.isInhibitor ?: false

            FoodEntryCard(
                entry = entry,
                foodItem = foodItem,
                isInhibitor = isInhibitor
            )
        }
    }
}

@Composable
fun FoodEntryCard(
    entry: FoodEntry,
    foodItem: FoodItem?,
    isInhibitor: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {

                // Warning icon esquina superior derecha


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nombre y cantidad
                    Column {
                        Text(
                            text = foodItem?.name ?: "Alimento desconocido",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${entry.quantity}${entry.unit.take(2)}", // "mg" o "ml"
                            fontSize = 13.sp,
                            color = Color(0xFF8B0000)
                        )
                    }

                    // Iron contributed
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isInhibitor) "0 mg"
                            else "${String.format(Locale.US, "%.2f", entry.ironContributed)} mg",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF8B0000)
                        )
                        if (isInhibitor) {
                            Text(
                                text = "Alerta: Inhibidor",
                                fontSize = 11.sp,
                                color = Color(0xFF8B0000)
                            )
                        }
                    }
                }
            }
        }
        if (isInhibitor) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-4).dp) // sobresale por arriba y derecha
                    .size(24.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(Color(0xFFFFE7E7))
            ) {
                Icon(
                    painter = painterResource(R.drawable.warning),
                    contentDescription = "Inhibidor",
                    tint = CrimsonDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

}