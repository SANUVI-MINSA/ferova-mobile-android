package pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.MealEntryHistory
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodDatabase
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodEntryDatabase
import pe.edu.upc.ferovafamily.presentation.theme.CrimsonDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionalHistoryScreen(
    selectedPatient: String,
    onBack: () -> Unit = {}
) {
    val foodEntries = FoodEntryDatabase.foodEntries
    val foodItems = FoodDatabase.foodItems


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
                        Icon(painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Volver",
                            tint = CrimsonDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF8F6))
            )
        }
    ) { paddingValues ->
        MealEntryHistory(
            patientName = selectedPatient,
            foodEntries = foodEntries,
            foodItems = foodItems,
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        )
    }
}

@Preview
@Composable
fun HistoryPreview() {
    NutritionalHistoryScreen(selectedPatient = "Lucia")
}