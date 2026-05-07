package pe.edu.upc.ferovafamily.presentation.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.ferovafamily.presentation.theme.Crimson
import pe.edu.upc.ferovafamily.presentation.theme.RoseBorder
import pe.edu.upc.ferovafamily.presentation.theme.RoseInput
import pe.edu.upc.ferovafamily.presentation.theme.TextDark
import pe.edu.upc.ferovafamily.presentation.theme.TextLight
import pe.edu.upc.ferovafamily.presentation.theme.TextMid
import pe.edu.upc.ferovafamily.presentation.theme.Crimson

@Composable
fun FerovaTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector? = null
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMid,
            letterSpacing = 0.3.sp
        )
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = TextLight,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Crimson,
                unfocusedBorderColor = RoseBorder,
                focusedContainerColor = RoseInput,
                unfocusedContainerColor = RoseInput,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark,
                focusedLabelColor = Crimson,
                unfocusedLabelColor = TextMid
            ),
            leadingIcon = icon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Crimson.copy(alpha = 0.7f)
                    )
                }
            },
            singleLine = true
        )
    }
}