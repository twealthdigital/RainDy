package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuroraEmerald
import com.example.ui.theme.RainBlueElectric
import com.example.ui.theme.RainCyan
import com.example.ui.theme.SunAmber

@Composable
fun WeeklyGoalProgressDashboard(
    completedTasks: Int,
    totalTasks: Int,
    outdoorGoalsTarget: Int = 7,
    outdoorGoalsDone: Int = 5,
    isDark: Boolean = true,
    modifier: Modifier = Modifier
) {
    val taskProgress = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks.toFloat()).coerceIn(0f, 1f) else 0.75f
    val outdoorProgress = (outdoorGoalsDone.toFloat() / outdoorGoalsTarget.toFloat()).coerceIn(0f, 1f)
    val climateReadiness = 0.92f

    val animTask by animateFloatAsState(
        targetValue = taskProgress,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "task_progress"
    )
    val animOutdoor by animateFloatAsState(
        targetValue = outdoorProgress,
        animationSpec = tween(1400, easing = FastOutSlowInEasing),
        label = "outdoor_progress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (isDark) Color(0x331E293B) else Color(0x99FFFFFF))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Weekly Activity & Goal Health",
                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Offline cached • Auto-syncs on reconnect",
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x2210B981))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "92% Prepared",
                    color = AuroraEmerald,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Concentric Progress Rings Canvas
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    val strokeWidth = 9.dp.toPx()
                    val center = Offset(size.width / 2, size.height / 2)

                    // Outer Ring: Task Completion
                    val outerRadius = (size.width - strokeWidth) / 2
                    drawCircle(
                        color = if (isDark) Color(0x2238BDF8) else Color(0x220284C7),
                        radius = outerRadius,
                        style = Stroke(strokeWidth)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(listOf(RainCyan, RainBlueElectric)),
                        startAngle = -90f,
                        sweepAngle = 360f * animTask,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(outerRadius * 2, outerRadius * 2)
                    )

                    // Middle Ring: Outdoor Activity Windows
                    val midStroke = 8.dp.toPx()
                    val midRadius = outerRadius - strokeWidth - 4.dp.toPx()
                    val midTopLeft = center.x - midRadius
                    drawCircle(
                        color = if (isDark) Color(0x22FBBF24) else Color(0x22D97706),
                        radius = midRadius,
                        style = Stroke(midStroke)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(listOf(SunAmber, Color(0xFFF97316))),
                        startAngle = -90f,
                        sweepAngle = 360f * animOutdoor,
                        useCenter = false,
                        style = Stroke(midStroke, cap = StrokeCap.Round),
                        topLeft = Offset(midTopLeft, midTopLeft),
                        size = Size(midRadius * 2, midRadius * 2)
                    )

                    // Inner Ring: Climate Resilience
                    val innerStroke = 7.dp.toPx()
                    val innerRadius = midRadius - midStroke - 4.dp.toPx()
                    val innerTopLeft = center.x - innerRadius
                    drawCircle(
                        color = if (isDark) Color(0x2210B981) else Color(0x22059669),
                        radius = innerRadius,
                        style = Stroke(innerStroke)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(listOf(AuroraEmerald, RainCyan)),
                        startAngle = -90f,
                        sweepAngle = 360f * climateReadiness,
                        useCenter = false,
                        style = Stroke(innerStroke, cap = StrokeCap.Round),
                        topLeft = Offset(innerTopLeft, innerTopLeft),
                        size = Size(innerRadius * 2, innerRadius * 2)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(taskProgress * 100).toInt()}%",
                        color = if (isDark) Color.White else Color(0xFF0F172A),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Goal",
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Ring Legends & Stats
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Task Ring item
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(RainCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Daily Tasks: $completedTasks / ${if (totalTasks > 0) totalTasks else 4}",
                            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Weather-aligned completion",
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    }
                }

                // Outdoor Ring item
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SunAmber)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Outdoor Goals: $outdoorGoalsDone / $outdoorGoalsTarget",
                            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Rain-free windows utilized",
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    }
                }

                // Climate Resilience
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AuroraEmerald)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Climate Alert Readiness",
                            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Active smart sync enabled",
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
