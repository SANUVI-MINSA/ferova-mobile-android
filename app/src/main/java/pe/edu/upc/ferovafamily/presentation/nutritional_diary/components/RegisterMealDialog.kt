package pe.edu.upc.ferovafamily.presentation.nutritional_diary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.ferovafamily.presentation.nutritional_diary.model.FoodItem

@Composable
fun RegisterMealDialog(
    foodItem: FoodItem,
    onDismiss: () -> Unit,
    onRegister: (quantity: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var quantity by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = foodItem.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B0000)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if(foodItem.isInhibitor)
                {
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFE7E7))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8B0000)
                                    )
                                ) {
                                    append("¡Advertencia! ")
                                }
                                withStyle(SpanStyle(color = Color(0xFF8B0000))) {
                                    append("${foodItem.name} puede reducir la absorción del suplemento de hierro.")
                                }
                            },
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
                Text(
                    text = "Cantidad",
                    fontSize = 16.sp,
                    color = Color(0xFF888888)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFFE7E7),
                            unfocusedContainerColor = Color(0xFFFFE7E7),
                            focusedBorderColor = Color(0xFF8B0000),
                            unfocusedBorderColor = Color(0xFFDDDDDD),
                            cursorColor = Color(0xFF8B0000),
                            focusedTextColor = Color(0xFF1A1A1A),
                            unfocusedTextColor = Color(0xFF1A1A1A)
                        ),
                        singleLine = true
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F0F0))
                            .padding(horizontal = 14.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = if (foodItem.category == "BEVERAGE" || foodItem.category == "DAIRY")
                                "Mililitros" else "Gramos",
                            fontSize = 13.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantity.toIntOrNull() ?: 0
                    onRegister(qty)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))
            ) {
                Text(
                    text = "Registrar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            // X en esquina - lo manejamos con el onDismissRequest del AlertDialog
        }
    )
}