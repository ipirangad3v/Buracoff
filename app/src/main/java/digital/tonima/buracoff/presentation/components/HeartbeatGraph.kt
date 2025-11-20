package digital.tonima.buracoff.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import digital.tonima.buracoff.domain.model.SensorDataPoint

@Composable
fun HeartbeatGraph(
    sensorData: List<SensorDataPoint>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF121212),
    normalColor: Color = Color(0xFF03DAC5),
    potholeColor: Color = Color(0xFFCF6679),
    gridColor: Color = Color(0xFF333333)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(backgroundColor)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        val gridLines = 5
        for (i in 0..gridLines) {
            val y = (height / gridLines) * i
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        drawLine(
            color = gridColor.copy(alpha = 0.5f),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 2f
        )

        if (sensorData.isEmpty()) {
            drawLine(
                color = normalColor,
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = 3f
            )
            return@Canvas
        }

        val maxMagnitude = 15.0
        val pointSpacing = if (sensorData.size > 1) width / (sensorData.size - 1) else width

        for (i in 0 until sensorData.size - 1) {
            val currentPoint = sensorData[i]
            val nextPoint = sensorData[i + 1]

            val x1 = i * pointSpacing
            val x2 = (i + 1) * pointSpacing

            val y1 = centerY - (currentPoint.magnitude.toFloat() / maxMagnitude.toFloat() * (height / 2) * 0.8f)
            val y2 = centerY - (nextPoint.magnitude.toFloat() / maxMagnitude.toFloat() * (height / 2) * 0.8f)

            val lineColor = if (currentPoint.isPothole || nextPoint.isPothole) potholeColor else normalColor

            val path = Path().apply {
                moveTo(x1, y1)

                val controlX = (x1 + x2) / 2
                quadraticTo(controlX, y1, x2, y2)
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 4f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            drawCircle(
                color = lineColor,
                radius = 3f,
                center = Offset(x1, y1)
            )
        }

        if (sensorData.isNotEmpty()) {
            val lastPoint = sensorData.last()
            val x = (sensorData.size - 1) * pointSpacing
            val y = centerY - (lastPoint.magnitude.toFloat() / maxMagnitude.toFloat() * (height / 2) * 0.8f)
            val color = if (lastPoint.isPothole) potholeColor else normalColor

            drawCircle(
                color = color,
                radius = 3f,
                center = Offset(x, y)
            )
        }
    }
}

