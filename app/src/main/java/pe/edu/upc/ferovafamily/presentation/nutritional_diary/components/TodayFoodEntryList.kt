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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.NutritionalDiaryViewModel
import pe.edu.upc.ferovafamily.domain.model.nutrition.FoodEntry
import pe.edu.upc.ferovafamily.presentation.theme.CrimsonDark
import java.util.Locale


@Composable
fun TodayFoodEntriesList(
    patientId: String,
    viewModel: NutritionalDiaryViewModel,
    modifier: Modifier = Modifier,
    patientName: String? = null
) {
    // ════════════════════════════════════════════════════════════════════════
    // ESTADOS DEL VIEWMODEL
    // ════════════════════════════════════════════════════════════════════════

    val todayDiary by viewModel.todayDiary.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // ════════════════════════════════════════════════════════════════════════
    // EFECTOS
    // ════════════════════════════════════════════════════════════════════════


    val todayEntries = todayDiary?.foodEntries ?: emptyList()
    val totalIronAbsorbed = todayDiary?.totalIronAbsorbed ?: 0.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ════════════════════════════════════════════════════════════════════
        // HEADER CON TOTAL DE HIERRO
        // ════════════════════════════════════════════════════════════════════

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Alimentos de Hoy",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B0000)
                )
                // Indica de qué niño es este diario
                if (!patientName.isNullOrBlank()) {
                    Text(
                        text = "de $patientName",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF888888)
                    )
                }
            }

            if (!isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFE7E7))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.1f", totalIronAbsorbed)} mg Fe",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B0000)
                    )
                }
            }
        }

        // ════════════════════════════════════════════════════════════════════
        // MOSTRAR ESTADO
        // ════════════════════════════════════════════════════════════════════

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
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFEBEE))
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

            todayEntries.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin alimentos registrados hoy",
                        fontSize = 14.sp,
                        color = Color(0xFF888888)
                    )
                }
            }

            else -> {
                // ════════════════════════════════════════════════════════════
                // LISTA DE ALIMENTOS
                // ════════════════════════════════════════════════════════════

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    todayEntries.forEach { entry ->
                        FoodEntryCard(foodEntry = entry, patientName = patientName)
                    }
                }
            }
        }
    }
}

@Composable
fun FoodEntryCard(
    foodEntry: FoodEntry,
    modifier: Modifier = Modifier,
    patientName: String? = null
) {
    // ════════════════════════════════════════════════════════════════════════
    // DETERMINAR SI ES INHIBIDOR Y COLOR DE FONDO
    // ════════════════════════════════════════════════════════════════════════

    val isInhibitor = foodEntry.isInhibitor
    val cardBgColor = if (isInhibitor) Color(0xFFFFF3E0) else Color.White
    val borderColor = if (isInhibitor) Color(0xFFFFB74D) else Color(0xFFE0E0E0)

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            elevation = cardElevation(defaultElevation = if (isInhibitor) 2.dp else 0.dp),
            border = BorderStroke(1.dp, borderColor)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ════════════════════════════════════════════════════════
                    // NOMBRE Y CANTIDAD
                    // ════════════════════════════════════════════════════════

                    Column {
                        Text(
                            text = foodEntry.foodName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${foodEntry.quantity} ${foodEntry.unit}",
                            fontSize = 13.sp,
                            color = Color(0xFF8B0000),
                            fontWeight = FontWeight.Medium
                        )
                        // Chip: indica de qué niño es esta comida
                        if (!patientName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFDECEC))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "👶 $patientName",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF8B0000)
                                )
                            }
                        }
                    }

                    // ════════════════════════════════════════════════════════
                    // HIERRO ABSORBIDO
                    // ════════════════════════════════════════════════════════

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${String.format(Locale.US, "%.2f", foodEntry.ironAbsorbed)} mg",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isInhibitor) Color(0xFFFF9800) else Color(0xFF8B0000)
                        )

                        if (isInhibitor) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFFE7E7))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Inhibidor",
                                    fontSize = 10.sp,
                                    color = Color(0xFF8B0000),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = "Absorbido",
                                fontSize = 11.sp,
                                color = Color(0xFF888888),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // ════════════════════════════════════════════════════════════════════
        // ICONO DE ADVERTENCIA EN LA ESQUINA SUPERIOR DERECHA
        // ════════════════════════════════════════════════════════════════════

        if (isInhibitor) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-4).dp)
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
                    contentDescription = "Alimento inhibidor",
                    tint = CrimsonDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
