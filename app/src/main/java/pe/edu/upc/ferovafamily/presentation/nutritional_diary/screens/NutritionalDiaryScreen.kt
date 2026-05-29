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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.NutritionalDiaryViewModel
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
    onSeeFoodHistory: (String) -> Unit = {},
    diaryViewModel: NutritionalDiaryViewModel = viewModel()
) {
    val diaryState by diaryViewModel.uiState.collectAsState()

    val patients        = diaryState.patients.ifEmpty { listOf("Sin hijos registrados") }
    val selectedPatient = diaryState.selectedPatient.ifBlank { patients.first() }

    val foodEntries = FoodEntryDatabase.foodEntries
    val foodItems   = FoodDatabase.foodItems
    val ironAbsorbed = foodEntries
        .filter { it.patientName == selectedPatient }
        .sumOf { it.ironContributed }
    val today = java.time.LocalDate.now().toString()

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
                selectedPatient = selectedPatient,
                totalIron = ironAbsorbed,
                patients = patients,
                onPatientSelected = { newName ->
                    diaryViewModel.selectPatient(newName)
                }
            )

            Spacer(Modifier.height(24.dp))

            TodayFoodEntriesList(
                patientName = selectedPatient,
                date = today,
                foodItems = foodItems,
                foodEntries = foodEntries
            )

            Spacer(Modifier.height(24.dp))

            ActionButtons(onNewFoodEntry, { onSeeFoodHistory(selectedPatient) })

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