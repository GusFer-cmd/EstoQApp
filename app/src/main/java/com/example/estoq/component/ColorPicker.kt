package com.example.estoq.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun ColorPicker(
    hue: Float,
    saturation: Float,
    brightness: Float,
    onHueChange: (Float) -> Unit,
    onSaturationBrightnessChange: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val sat = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                        val bri = (1f - offset.y / size.height.toFloat()).coerceIn(0f, 1f)
                        onSaturationBrightnessChange(sat, bri)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val sat = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                            val bri = (1f - offset.y / size.height.toFloat()).coerceIn(0f, 1f)
                            onSaturationBrightnessChange(sat, bri)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val sat = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                            val bri = (1f - change.position.y / size.height.toFloat()).coerceIn(0f, 1f)
                            onSaturationBrightnessChange(sat, bri)
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val pureColor = Color.hsv(hue, 1f, 1f)

                drawRect(color = pureColor)

                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.White, Color.Transparent)
                    )
                )

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black)
                    )
                )

                val indicatorX = saturation * size.width
                val indicatorY = (1f - brightness) * size.height

                drawCircle(
                    color = Color.Black,
                    radius = 8f,
                    center = Offset(indicatorX, indicatorY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 6f,
                    center = Offset(indicatorX, indicatorY)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val h = (offset.x / size.width.toFloat() * 360f).coerceIn(0f, 360f)
                        onHueChange(h)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val h = (offset.x / size.width.toFloat() * 360f).coerceIn(0f, 360f)
                            onHueChange(h)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val h = (change.position.x / size.width.toFloat() * 360f).coerceIn(0f, 360f)
                            onHueChange(h)
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.00f to Color.hsv(0f, 1f, 1f),
                            0.17f to Color.hsv(60f, 1f, 1f),
                            0.33f to Color.hsv(120f, 1f, 1f),
                            0.50f to Color.hsv(180f, 1f, 1f),
                            0.67f to Color.hsv(240f, 1f, 1f),
                            0.83f to Color.hsv(300f, 1f, 1f),
                            1.00f to Color.hsv(360f, 1f, 1f),
                        )
                    )
                )

                val indicatorX = hue / 360f * size.width
                drawLine(
                    color = Color.White,
                    start = Offset(indicatorX, 0f),
                    end = Offset(indicatorX, size.height),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color.Black,
                    start = Offset(indicatorX, 0f),
                    end = Offset(indicatorX, size.height),
                    strokeWidth = 1f
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val color = Color.hsv(hue, saturation, brightness)
            Text(
                text = "R: ${(color.red * 255).roundToInt()}  " +
                       "G: ${(color.green * 255).roundToInt()}  " +
                       "B: ${(color.blue * 255).roundToInt()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.hsv(hue, saturation, brightness))
        )
    }
}
