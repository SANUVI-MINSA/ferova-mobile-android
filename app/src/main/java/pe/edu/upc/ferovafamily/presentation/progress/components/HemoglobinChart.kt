package pe.edu.upc.ferovafamily.presentation.progress.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.ferovafamily.presentation.progress.model.HemoglobinPoint

private val Crimson = Color(0xFF8B1A1A)
private val GridGray = Color(0xFFE0E0E0)

@Composable
fun HemoglobinChart(
    points: List<HemoglobinPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val padLeft = 24.dp.toPx()
        val padRight = 16.dp.toPx()
        val padTop = 28.dp.toPx()
        val padBottom = 32.dp.toPx()

        val chartWidth = size.width - padLeft - padRight
        val chartHeight = size.height - padTop - padBottom

        val maxValue = (points.maxOf { it.value } + 1f).coerceAtLeast(12f)
        val minValue = (points.minOf { it.value } - 1f).coerceAtLeast(0f)
        val valueRange = (maxValue - minValue).coerceAtLeast(1f)

        // Líneas de grid horizontales (punteadas)
        val gridLines = 4
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        for (i in 0..gridLines) {
            val y = padTop + (chartHeight * i / gridLines)
            drawLine(
                color = GridGray,
                start = Offset(padLeft, y),
                end = Offset(size.width - padRight, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashEffect
            )
        }

        // Coordenadas de cada punto
        val coords = points.mapIndexed { index, point ->
            val x = padLeft + (chartWidth * index / (points.size - 1).coerceAtLeast(1))
            val y = padTop + chartHeight * (1f - (point.value - minValue) / valueRange)
            Offset(x, y)
        }

        // Línea conectando puntos
        val linePath = Path().apply {
            coords.forEachIndexed { i, p ->
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
        }
        drawPath(
            path = linePath,
            color = Crimson,
            style = Stroke(width = 2.5.dp.toPx())
        )

        // Puntos (círculo blanco con borde crimson)
        coords.forEach { p ->
            drawCircle(color = Color.White, radius = 5.dp.toPx(), center = p)
            drawCircle(
                color = Crimson,
                radius = 5.dp.toPx(),
                center = p,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Etiquetas: valor encima de cada punto y fecha debajo del eje X
        val valuePaint = android.graphics.Paint().apply {
            color = Crimson.toArgb()
            textSize = 11.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }
        val labelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }

        coords.forEachIndexed { index, p ->
            drawContext.canvas.nativeCanvas.drawText(
                "%.1f".format(points[index].value),
                p.x,
                p.y - 10.dp.toPx(),
                valuePaint
            )
            drawContext.canvas.nativeCanvas.drawText(
                points[index].dateLabel,
                p.x,
                size.height - 8.dp.toPx(),
                labelPaint
            )
        }
    }
}