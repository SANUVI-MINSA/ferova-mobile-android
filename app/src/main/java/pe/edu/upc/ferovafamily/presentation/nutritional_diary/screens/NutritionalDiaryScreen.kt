package pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.ActionButtons
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.IronAbsorptionCard
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.TipCard
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.TodayFoodEntriesList
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodDatabase
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodEntryDatabase
import pe.edu.upc.ferovafamily.presentation.theme.Crimson
import pe.edu.upc.ferovafamily.presentation.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionalDiaryScreen(
    onNewFoodEntry: () -> Unit = {},
    onSeeFoodHistory: () -> Unit = {},
) {
    val selectedPatient = remember {
        mutableStateOf("Mateo")
    }
    val patients = listOf("Mateo", "Lucia")
    val foodEntries = FoodEntryDatabase.foodEntries
    val foodItems = FoodDatabase.foodItems
    val ironAbsorbed = FoodEntryDatabase.foodEntries
        .filter { it.patientName == selectedPatient.value }
        .sumOf { it.ironContributed }
    val today = "2026-05-11"

    Scaffold(
        containerColor = White,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))

            IronAbsorptionCard(
                selectedPatient = selectedPatient.value,
                totalIron = ironAbsorbed,
                patients = patients,
                onPatientSelected = { newName ->
                    selectedPatient.value = newName
                }
            )

            Spacer(Modifier.height(24.dp))

            TodayFoodEntriesList(
                patientName = selectedPatient.value,
                date = today,
                foodItems = foodItems,
                foodEntries = foodEntries
            )

            Spacer(Modifier.height(24.dp))

            ActionButtons(onNewFoodEntry, onSeeFoodHistory)

            Spacer(Modifier.height(24.dp))

            TipCard(
                titleTipText = "Tip de hoy",
                tipText = "Combina las lentajas con citricos\n" +
                        "(Vitamina C) para mejorar la absorcion del hierro.",
                titleTipStyle = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                tipTextStyle = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    lineHeight = 20.sp
                ),
            )

            Spacer(Modifier.height(24.dp))

        }
    }
}

@Preview
@Composable
fun NutritionalDiaryPreview() {
    NutritionalDiaryScreen()
}