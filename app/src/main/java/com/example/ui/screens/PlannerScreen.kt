package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RainDyViewModel
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.MorphismStyle
import com.example.ui.components.popOnTap
import com.example.ui.theme.AlertRose
import com.example.ui.theme.AuroraEmerald
import com.example.ui.theme.RainBlueElectric
import com.example.ui.theme.RainCyan
import com.example.ui.theme.SunAmber

@Composable
fun PlannerScreen(
    viewModel: RainDyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark by viewModel.isDarkMode.collectAsState()
    val recommendations by viewModel.activityRecommendations.collectAsState()
    val currentCity by viewModel.currentCity.collectAsState()
    val morphismStyle by viewModel.morphismStyle.collectAsState()
    val climateColor by viewModel.climateThemeColor.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(climateColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Planner",
                        tint = climateColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Smart Weather Planner",
                        color = if (isDark) Color.White else Color(0xFF0F172A),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Calendar-synced outdoor activity optimizer",
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Feature Banner
        item {
            LiquidGlassCard(
                isDark = isDark,
                morphismStyle = morphismStyle,
                accentGlow = climateColor,
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(climateColor.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = null,
                            tint = climateColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Automated Rain-Safe Scheduling",
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "RainDy calculates dry windows across $currentCity and suggests safe slots with 1-click calendar sync.",
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Recommended Outdoor Windows",
                color = if (isDark) Color.White else Color(0xFF0F172A),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(recommendations) { rec ->
            val badgeBg = when (rec.weatherSuitability) {
                "Optimal" -> AuroraEmerald.copy(alpha = 0.2f)
                "Good" -> climateColor.copy(alpha = 0.2f)
                else -> AlertRose.copy(alpha = 0.2f)
            }
            val badgeText = when (rec.weatherSuitability) {
                "Optimal" -> AuroraEmerald
                "Good" -> climateColor
                else -> AlertRose
            }

            LiquidGlassCard(
                isDark = isDark,
                morphismStyle = morphismStyle,
                accentGlow = climateColor,
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth().testTag("activity_card_${rec.activityName.take(5)}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = rec.icon,
                                fontSize = 26.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = rec.activityName,
                                    color = if (isDark) Color.White else Color(0xFF0F172A),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = rec.bestTimeWindow,
                                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(badgeBg)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = rec.weatherSuitability,
                                color = badgeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = rec.reason,
                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sync to Calendar Button with Pop on Tap
                    ElevatedButton(
                        onClick = {
                            val success = viewModel.syncRecommendationToCalendar(context, rec)
                            if (success) {
                                Toast.makeText(context, "Opening Calendar for ${rec.activityName}...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Could not open calendar.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = climateColor,
                            contentColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .popOnTap {
                                val success = viewModel.syncRecommendationToCalendar(context, rec)
                                if (success) {
                                    Toast.makeText(context, "Opening Calendar for ${rec.activityName}...", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .testTag("sync_to_calendar_btn_${rec.activityName.take(5)}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Safe Window to Calendar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
