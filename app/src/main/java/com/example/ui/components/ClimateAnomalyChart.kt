package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GlobalClimateTrendPoint
import com.example.ui.theme.AlertRose
import com.example.ui.theme.RainCyan
import com.example.ui.theme.SunAmber

@Composable
fun ClimateAnomalyChart(
    points: List<GlobalClimateTrendPoint>,
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    if (points.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(points.size - 1) }
    val selectedPoint = selectedIndex?.let { points.getOrNull(it) } ?: points.last()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (isDark) Color(0x331E293B) else Color(0x99FFFFFF))
            .padding(16.dp)
    ) {
        // Selected point indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Global Temp Anomaly (${selectedPoint.year})",
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "+${"%.2f".format(selectedPoint.temperatureAnomaly)} °C",
                    color = AlertRose,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Atmospheric CO₂",
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${"%.1f".format(selectedPoint.co2Ppm)} ppm",
                    color = RainCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Canvas Curve
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            val width = size.width
                            val step = width / (points.size - 1).coerceAtLeast(1)
                            val idx = (offset.x / step).toInt().coerceIn(0, points.size - 1)
                            selectedIndex = idx
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                val paddingBottom = 24.dp.toPx()
                val paddingTop = 16.dp.toPx()
                val usableHeight = h - paddingBottom - paddingTop

                val minAnomaly = 0.0
                val maxAnomaly = 1.8

                val pointOffsets = points.mapIndexed { index, point ->
                    val x = (w / (points.size - 1)) * index
                    val normY = ((point.temperatureAnomaly - minAnomaly) / (maxAnomaly - minAnomaly))
                        .coerceIn(0.0, 1.0)
                    val y = h - paddingBottom - (normY.toFloat() * usableHeight)
                    Offset(x, y)
                }

                // Baseline 0°C (Pre-industrial reference line)
                val baselineY = h - paddingBottom
                drawLine(
                    color = if (isDark) Color(0x44FFFFFF) else Color(0x330F172A),
                    start = Offset(0f, baselineY),
                    end = Offset(w, baselineY),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                // Critical 1.5°C Paris Agreement Threshold line
                val threshold15Y = h - paddingBottom - (((1.5 - minAnomaly) / (maxAnomaly - minAnomaly)).toFloat() * usableHeight)
                drawLine(
                    color = Color(0x66F43F5E),
                    start = Offset(0f, threshold15Y),
                    end = Offset(w, threshold15Y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )

                // Build Curve Path
                val curvePath = Path().apply {
                    if (pointOffsets.isNotEmpty()) {
                        moveTo(pointOffsets.first().x, pointOffsets.first().y)
                        for (i in 0 until pointOffsets.size - 1) {
                            val p0 = pointOffsets[i]
                            val p1 = pointOffsets[i + 1]
                            val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                            val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)
                            cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                        }
                    }
                }

                // Fill area under curve
                val fillPath = Path().apply {
                    addPath(curvePath)
                    lineTo(pointOffsets.last().x, h - paddingBottom)
                    lineTo(pointOffsets.first().x, h - paddingBottom)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x66F43F5E),
                            Color(0x22FBBF24),
                            Color(0x0038BDF8)
                        ),
                        startY = paddingTop,
                        endY = h - paddingBottom
                    )
                )

                // Draw curve stroke
                drawPath(
                    path = curvePath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF38BDF8),
                            Color(0xFFFBBF24),
                            Color(0xFFF43F5E)
                        )
                    ),
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw dot markers
                pointOffsets.forEachIndexed { idx, offset ->
                    val isCurrentSelected = idx == selectedIndex
                    val radius = if (isCurrentSelected) 6.dp.toPx() else 3.5.dp.toPx()
                    drawCircle(
                        color = if (isCurrentSelected) Color.White else Color(0xFFF43F5E),
                        radius = radius,
                        center = offset
                    )
                    if (isCurrentSelected) {
                        drawCircle(
                            color = AlertRose,
                            radius = 10.dp.toPx(),
                            center = offset,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }

        // Timeline labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.filterIndexed { index, _ -> index % 3 == 0 || index == points.size - 1 }.forEach { pt ->
                Text(
                    text = pt.year.toString(),
                    color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
