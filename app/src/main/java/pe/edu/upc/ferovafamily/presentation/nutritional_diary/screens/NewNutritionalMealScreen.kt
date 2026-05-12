package pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.MealCatalog
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.MealSearch
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodDatabase
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodItem
import pe.edu.upc.ferovafamily.presentation.theme.CrimsonDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewNutritionalMealScreen(
    onBack: () -> Unit = {},
    onRegisterMeal: () -> Unit = {},
) {

    val selectedMeal = remember {
        mutableStateOf<FoodItem?>(null)
    }
    val searchedMeal = remember {
        mutableStateOf("")
    }
    val foodItems = FoodDatabase.foodItems



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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            Spacer(Modifier.height(16.dp))

            MealSearch(
                searchedMeal.value,
                {searchedMeal.value = it}
            )

            Spacer(Modifier.height(16.dp))

            MealCatalog(
                foodItems = foodItems,
            )
        }
    }
}

@Preview
@Composable
fun NewMealPreview() {
    NewNutritionalMealScreen()
}