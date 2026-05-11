package pe.edu.upc.ferovafamily.presentation.nutritional_diary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import pe.edu.upc.ferovafamily.R
import pe.edu.upc.ferovafamily.presentation.theme.Crimson

@Composable
fun TipCard(
    modifier: Modifier = Modifier,
    titleTipText: String = "",
    tipText: String = "",
    containerColor: Color = Color(0xFFFCECEC),
    bulbColor: Color = Crimson,
    titleTipStyle: TextStyle,
    tipTextStyle: TextStyle
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.lightbulb),
                    contentDescription = "Tip del día",
                    tint = bulbColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = titleTipText,
                    style = titleTipStyle
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row()
            {
                Spacer(Modifier.width(32.dp))

                Text(
                    text = tipText,
                    style = tipTextStyle
                )
            }

        }
    }
}