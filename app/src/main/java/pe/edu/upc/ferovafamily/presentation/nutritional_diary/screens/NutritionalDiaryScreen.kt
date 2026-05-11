package pe.edu.upc.ferovafamily.presentation.nutritional_diary.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.components.TipCard
import pe.edu.upc.ferovafamily.presentation.theme.Crimson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionalDiaryScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                title = {
                    Text(
                        text = "Diario Nutricional",
                        color = Crimson,
                        fontWeight = FontWeight.Bold
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
        }
    }
}

@Preview
@Composable
fun NutritionalDiaryPreview() {
    NutritionalDiaryScreen()
}