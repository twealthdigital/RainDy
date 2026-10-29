package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RainDyViewModel
import com.example.ui.components.ClimateAnomalyChart
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.popOnTap
import com.example.ui.theme.AlertRose
import com.example.ui.theme.AuroraEmerald
import com.example.ui.theme.MetricPillBorderDark
import com.example.ui.theme.MetricPillBorderLight
import com.example.ui.theme.MetricPillDark
import com.example.ui.theme.MetricPillLight
import com.example.ui.theme.RainCyan
import com.example.ui.theme.SunAmber

@Composable
fun ClimateTrendsScreen(
    viewModel: RainDyViewModel,
    modifier: Modifier = Modifier
) {
    val isDark by viewModel.isDarkMode.collectAsState()
    val points = viewModel.globalClimatePoints
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
                        imageVector = Icons.Default.Public,
                        contentDescription = "Climate Dashboard",
                        tint = climateColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Global Climate Observatory",
                        color = if (isDark) Color.White else Color(0xFF0F172A),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time trends, anomalies & planetary vital signs",
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Hero Climate Anomaly Chart Card
        item {
            LiquidGlassCard(
                isDark = isDark,
                morphismStyle = morphismStyle,
                accentGlow = climateColor,
                elevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("climate_trends_summary_card")
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
                        Column {
                            Text(
                                text = "GLOBAL WARMING TRAJECTORY",
                                color = climateColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "+1.51 °C above Pre-Industrial",
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AlertRose.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Paris 1.5° Limit",
                                color = AlertRose,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ClimateAnomalyChart(
                        points = points,
                        isDark = isDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Tap along the chart timeline to observe atmospheric carbon concentration and surface anomalies accelerating across decades.",
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Key Planetary Vital Signs
        item {
            Text(
                text = "Planetary Vital Signs (2026)",
                color = if (isDark) Color.White else Color(0xFF0F172A),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Carbon concentration
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (isDark) MetricPillDark else MetricPillLight)
                        .border(0.5.dp, if (isDark) MetricPillBorderDark else MetricPillBorderLight, RoundedCornerShape(22.dp))
                        .popOnTap {}
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Atmospheric CO₂",
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "426.8 ppm",
                            color = climateColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+2.4 ppm/yr rate",
                            color = AlertRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Arctic Sea Ice
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (isDark) MetricPillDark else MetricPillLight)
                        .border(0.5.dp, if (isDark) MetricPillBorderDark else MetricPillBorderLight, RoundedCornerShape(22.dp))
                        .popOnTap {}
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Arctic Ice Anomaly",
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "-2.9M km²",
                            color = Color(0xFF818CF8),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "-12.6% per decade",
                            color = AlertRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Extreme Weather Shifts Card
        item {
            LiquidGlassCard(
                isDark = isDark,
                morphismStyle = morphismStyle,
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = SunAmber,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Extreme Weather Multipliers",
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "⛈️ Heavy Precipitation Events",
                                color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "+28% frequency",
                                color = AlertRose,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🔥 Extreme Heatwave Days",
                                color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "3.2x baseline",
                                color = AlertRose,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🌊 Sea Level Rise Acceleration",
                                color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "4.4 mm / year",
                                color = RainCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Local Micro-Climate Context for selected city
        item {
            LiquidGlassCard(
                isDark = isDark,
                morphismStyle = morphismStyle,
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Local Baseline Shift • $currentCity",
                        color = climateColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Micro-Climate Trend Profile",
                        color = if (isDark) Color.White else Color(0xFF0F172A),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Climate observations show $currentCity experiencing more concentrated precipitation windows separated by longer dry stretches compared to the 1980-2010 baseline. RainDy's predictive bubble notifications help you navigate these transitions with precision.",
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
