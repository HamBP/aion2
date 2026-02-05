package me.algosketch.aioninfo.presentation.feature.enhancement

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DualCdfChart(
    stonesData: List<Int>,
    kinaData: List<Int>,
    modifier: Modifier = Modifier,
    stonesLabel: String = "강화석",
    kinaLabel: String = "키나",
) {
    val stonesColor = Color(0xFF2196F3) // Blue
    val kinaColor = Color(0xFFFF9800) // Orange

    if (stonesData.isEmpty() || kinaData.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text("No data", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        return
    }

    val sortedStones = stonesData.sorted()
    val sortedKina = kinaData.sorted()

    val stonesMin = sortedStones.first()
    val stonesMax = sortedStones.last()
    val stonesRange = (stonesMax - stonesMin).coerceAtLeast(1)

    val kinaMin = sortedKina.first()
    val kinaMax = sortedKina.last()
    val kinaRange = (kinaMax - kinaMin).coerceAtLeast(1)

    val textMeasurer = rememberTextMeasurer()
    val axisColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

    Box(modifier = modifier.fillMaxWidth()) {
        // Legend
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawRect(color = stonesColor)
                }
                Text(
                    text = " $stonesLabel",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawRect(color = kinaColor)
                }
                Text(
                    text = " $kinaLabel",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(top = 24.dp)
        ) {
            val paddingLeft = 50.dp.toPx()
            val paddingRight = 20.dp.toPx()
            val paddingTop = 10.dp.toPx()
            val paddingBottom = 70.dp.toPx()

            val chartWidth = size.width - paddingLeft - paddingRight
            val chartHeight = size.height - paddingTop - paddingBottom

            // Draw grid lines
            for (i in 0..4) {
                val y = paddingTop + chartHeight * (1 - i / 4f)
                drawLine(
                    color = gridColor,
                    start = Offset(paddingLeft, y),
                    end = Offset(paddingLeft + chartWidth, y),
                    strokeWidth = 1f
                )
            }

            // Draw Y-axis
            drawLine(
                color = axisColor,
                start = Offset(paddingLeft, paddingTop),
                end = Offset(paddingLeft, paddingTop + chartHeight),
                strokeWidth = 2f
            )

            // Draw bottom X-axis (강화석)
            drawLine(
                color = axisColor,
                start = Offset(paddingLeft, paddingTop + chartHeight),
                end = Offset(paddingLeft + chartWidth, paddingTop + chartHeight),
                strokeWidth = 2f
            )

            // Draw Y-axis labels (0%, 25%, 50%, 75%, 100%)
            for (i in 0..4) {
                val percent = i * 25
                val y = paddingTop + chartHeight * (1 - i / 4f)
                val label = "$percent%"
                val textLayoutResult = textMeasurer.measure(
                    text = label,
                    style = TextStyle(fontSize = 10.sp)
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        paddingLeft - textLayoutResult.size.width - 8.dp.toPx(),
                        y - textLayoutResult.size.height / 2
                    )
                )
            }

            // Draw bottom X-axis labels (강화석 - blue)
            val xLabelCount = 5
            for (i in 0 until xLabelCount) {
                val value = stonesMin + stonesRange * i / (xLabelCount - 1)
                val x = paddingLeft + chartWidth * i / (xLabelCount - 1)
                val label = formatNumber(value)
                val textLayoutResult = textMeasurer.measure(
                    text = label,
                    style = TextStyle(fontSize = 10.sp, color = stonesColor)
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x - textLayoutResult.size.width / 2,
                        paddingTop + chartHeight + 8.dp.toPx()
                    )
                )
            }

            // Draw top X-axis labels (키나 - orange)
            for (i in 0 until xLabelCount) {
                val value = kinaMin + kinaRange * i / (xLabelCount - 1)
                val x = paddingLeft + chartWidth * i / (xLabelCount - 1)
                val label = formatNumber(value)
                val textLayoutResult = textMeasurer.measure(
                    text = label,
                    style = TextStyle(fontSize = 10.sp, color = kinaColor)
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x - textLayoutResult.size.width / 2,
                        paddingTop + chartHeight + 28.dp.toPx()
                    )
                )
            }

            // Draw CDF line for stones
            drawCdfLine(
                sortedData = sortedStones,
                minValue = stonesMin,
                range = stonesRange,
                color = stonesColor,
                paddingLeft = paddingLeft,
                paddingTop = paddingTop,
                chartWidth = chartWidth,
                chartHeight = chartHeight
            )

            // Draw CDF line for kina
            drawCdfLine(
                sortedData = sortedKina,
                minValue = kinaMin,
                range = kinaRange,
                color = kinaColor,
                paddingLeft = paddingLeft,
                paddingTop = paddingTop,
                chartWidth = chartWidth,
                chartHeight = chartHeight
            )
        }
    }
}

private fun DrawScope.drawCdfLine(
    sortedData: List<Int>,
    minValue: Int,
    range: Int,
    color: Color,
    paddingLeft: Float,
    paddingTop: Float,
    chartWidth: Float,
    chartHeight: Float
) {
    val path = Path()
    val totalCount = sortedData.size

    sortedData.forEachIndexed { index, value ->
        val x = paddingLeft + chartWidth * (value - minValue).toFloat() / range
        val y = paddingTop + chartHeight * (1 - (index + 1).toFloat() / totalCount)

        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun formatNumber(value: Int): String {
    return when {
        value >= 1_000_000 -> {
            val millions = value / 1_000_000.0
            "${(millions * 10).toInt() / 10.0}M"
        }
        value >= 1_000 -> "${value / 1_000}K"
        else -> value.toString()
    }
}
