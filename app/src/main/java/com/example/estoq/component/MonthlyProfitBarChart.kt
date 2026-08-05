package com.example.estoq.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estoq.data.Model.PivotSaleItem.MonthlyProfitSummary
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun MonthlyProfitBarChart(
    data: List<MonthlyProfitSummary>,
    selectedIndex: Int?,
    onBarClick: (Int) -> Unit
) {
    val months = remember(data) { data.sortedBy { it.monthKey } }
    val textMeasurer = rememberTextMeasurer()

    if (months.isEmpty()) return

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    val positiveMax = months.maxOfOrNull { it.profit } ?: 0.0
    val negativeMin = months.minOfOrNull { it.profit } ?: 0.0
    val maxValue = max(positiveMax, abs(negativeMin)).takeIf { it > 0.0 } ?: 1.0
    val hasNegative = negativeMin < 0

    val topSpace = 18.dp
    val bottomSpace = 22.dp

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .pointerInput(months.size) {
                    detectTapGestures { offset ->
                        val slot = size.width / months.size
                        val index = (offset.x / slot).toInt().coerceIn(0, months.size - 1)
                        onBarClick(index)
                    }
                }
        ) {
            val slotWidth = size.width / months.size
            val barWidth = slotWidth * 0.55f
            val chartTop = topSpace.toPx()
            val chartBottom = size.height - bottomSpace.toPx()
            val chartHeight = chartBottom - chartTop

            val baselineY = if (hasNegative) {
                chartTop + chartHeight * (positiveMax / (positiveMax - negativeMin)).toFloat()
            } else {
                chartBottom
            }

            months.forEachIndexed { index, month ->
                val ratio = (month.profit / maxValue).toFloat()
                val barHeight = ratio * chartHeight
                val left = slotWidth * index + (slotWidth - barWidth) / 2f
                val isSelected = index == selectedIndex

                val barColor = when {
                    month.profit < 0 -> Color(0xFFC62828)
                    isSelected -> Color(0xFF355872)
                    else -> Color(0xFF43A047)
                }

                val top = baselineY - barHeight
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )

                val textLayout = textMeasurer.measure(
                    text = compactCurrency(month.profit),
                    style = TextStyle(
                        color = if (isSelected) {
                            onSurfaceColor
                        } else {
                            onSurfaceVariantColor.copy(alpha = 0.7f)
                        },
                        fontSize = 9.sp
                    )
                )
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        (left + barWidth / 2f) - textLayout.size.width / 2f,
                        top - textLayout.size.height - 2.dp.toPx()
                    )
                )
            }

            if (hasNegative) {
                drawLine(
                    color = Color.Gray.copy(alpha = 0.6f),
                    start = Offset(0f, baselineY),
                    end = Offset(size.width, baselineY),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            months.forEachIndexed { index, month ->
                Text(
                    text = shortMonthLabel(month.monthKey),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (index == selectedIndex) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private val monthShortNames = arrayOf(
    "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
    "Jul", "Ago", "Set", "Out", "Nov", "Dez"
)

private fun shortMonthLabel(monthKey: String): String {
    val month = monthKey.substringAfter('-').toIntOrNull() ?: return monthKey
    return monthShortNames.getOrElse(month - 1) { monthKey }
}

private fun compactCurrency(value: Double): String {
    val absValue = abs(value)
    val signal = if (value < 0) "-" else ""
    return when {
        absValue >= 1_000_000 -> "$signal${formatCompact(absValue / 1_000_000)}M"
        absValue >= 1_000 -> "$signal${formatCompact(absValue / 1_000)}k"
        else -> "$signal${absValue.roundToInt()}"
    }
}

private fun formatCompact(value: Double): String =
    if (value % 1.0 == 0.0) value.roundToInt().toString()
    else String.format(Locale.US, "%.1f", value)
