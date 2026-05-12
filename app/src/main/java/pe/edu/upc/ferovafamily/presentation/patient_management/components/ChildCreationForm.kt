package pe.edu.upc.ferovafamily.presentation.patient_management.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.presentation.patient_management.screens.ChildData
import pe.edu.upc.ferovafamily.presentation.theme.Crimson
import pe.edu.upc.ferovafamily.presentation.theme.White

@Composable
fun ChildCreationForm(
    onRegister: (ChildData) -> Unit
) {
    val name = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val birthDate = remember { mutableStateOf("") }
    val gender = remember { mutableStateOf("Masculino") }
    val weightInput = remember { mutableStateOf("") }
    val heightInput = remember { mutableStateOf("") }

    val weight: Double = weightInput.value.toDoubleOrNull() ?: 0.0
    val height: Int = heightInput.value.toIntOrNull() ?: 0

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            )
            {
                Spacer(Modifier.height(16.dp))
                Text("Nombres del niño/a")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = name.value, placeholder = {
                    Text("Ej. Diana Lucia", fontWeight = FontWeight.Light, fontSize = 12.sp)
                }, onValueChange = {
                    name.value = it
                }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(16.dp))
                Text("Apellidos")
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(value = lastName.value, placeholder = {
                    Text("Ej. Briceño Vera", fontWeight = FontWeight.Light, fontSize = 12.sp)
                }, onValueChange = {
                    lastName.value = it
                }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(16.dp))
                Text("Fecha de nacimiento")
                Spacer(Modifier.height(8.dp))

                DatePickerTextField(
                    value = birthDate.value,
                    onDateSelected = { birthDate.value = it },
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf("Masculino", "Femenino").forEach { genero ->

                        val isSelected = gender.value == genero
                        val color = if (isSelected) Crimson else Color.LightGray

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { gender.value = genero },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = color
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFFFF5F5) else Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = if (genero == "Masculino")
                                        painterResource(R.drawable.male) else painterResource(R.drawable.female),
                                    contentDescription = genero,
                                    tint = color,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = genero,
                                    color = color,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Peso Actual (KG)")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = weightInput.value,
                            onValueChange = { weightInput.value = it },
                            placeholder = { Text("0.0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Talla/ Altura (CM)")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = heightInput.value,
                            onValueChange = { heightInput.value = it },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Crimson,
                contentColor = White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 18.dp),
            onClick = {
                val nameError = name.value.isBlank()
                val lastNameError = lastName.value.isBlank()
                val birthDateError = birthDate.value.isBlank()
                val weightError = weightInput.value.isBlank()
                val heightError = heightInput.value.isBlank()

                val isAnyError = nameError || lastNameError || birthDateError ||
                        weightError || heightError
                if (isAnyError) return@Button

                onRegister(
                    ChildData(
                        name = name.value,
                        lastName = lastName.value,
                        birthDate = birthDate.value,
                        gender = gender.value,
                        weight = weight,
                        height = height
                    )
                )
            }
        ) {
            Text("Registrar a mi pequeño", fontSize = 16.sp, fontWeight = FontWeight.Normal)
            Spacer(Modifier.width(24.dp))
            AsyncImage(
                model = R.drawable.favorite, "heart",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}