package pe.edu.upc.ferovafamily.presentation.patient_management.components

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "mm/dd/yyyy",
    dateFormat: String = "MM/dd/yyyy",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(modifier = modifier) {

        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(
                    width = 1.dp,
                    color = when {
                        isError -> MaterialTheme.colorScheme.error
                        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.outline
                    },
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable(enabled = enabled) { showPicker = true }
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = value.ifEmpty { placeholder },
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 0.5.sp,
                    color = if (value.isEmpty())
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    else
                        MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = "Select date",
                tint = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }

        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
            )
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
                            onDateSelected(sdf.format(Date(millis)))
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}