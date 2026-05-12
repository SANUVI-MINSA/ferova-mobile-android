package pe.edu.upc.ferovafamily.presentation.nutritional_diary.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Shapes
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import pe.edu.upc.ferovafamily.R
import java.util.Locale

@Composable
fun IronAbsorptionCard(
    selectedPatient: String,
    totalIron: Double,
    patients: List<String>,
    onPatientSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            PatientChip(
                patients = patients,
                selectedPatient = selectedPatient,
                onPatientSelected = onPatientSelected
            )
            Spacer(Modifier.width(16.dp))
        }


        Spacer(modifier = Modifier.height(24.dp))

        IronCircle(totalIron = totalIron)

    }


}

@Composable
fun PatientChip(
    patients: List<String>,
    selectedPatient: String,
    onPatientSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Chip principal
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Transparent)
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCCCCCC))
            )
            Text(
                text = selectedPatient,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
            Icon(
                painter = painterResource(if (expanded) R.drawable.arrow_drop_up
                else R.drawable.arrow_drop_down),
                contentDescription = null,
                tint = Color(0xFF8B0000),
                modifier = Modifier.size(32.dp)
            )
        }

        // Dropdown
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color(0xFFF9F5F3)
        ) {
            patients.filter { it != selectedPatient }.forEach { patient ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFCCCCCC))
                            )
                            Text(
                                text = patient,
                                fontSize = 15.sp,
                                color = Color(0xFF1A1A1A)
                            )
                        }
                    },
                    onClick = {
                        onPatientSelected(patient)
                        expanded = false
                    },
                    contentPadding = PaddingValues(start = 16.dp, top = 8.dp, bottom = 8.dp,
                        end = 48.dp)
                )
            }
        }
    }
}

@Composable
fun IronCircle(
    totalIron: Double,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(220.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF8B0000),
                radius = size.minDimension / 2,
                style = Stroke(width = 12.dp.toPx())
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HIERRO ABSORBIDO",
                fontSize = 11.sp,
                color = Color(0xFF555555),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format(Locale.US, "%.2f", totalIron),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B0000)
                )
                Text(
                    text = "/mg",
                    fontSize = 18.sp,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}