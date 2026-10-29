package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.WeatherRepository
import com.example.ui.RainDyViewModel
import com.example.ui.WeatherUiState
import com.example.ui.components.AnimatedWeatherArt
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.MorphismStyle
import com.example.ui.components.popOnTap
import com.example.ui.theme.AlertRose
import com.example.ui.theme.AuroraEmerald
import com.example.ui.theme.MetricPillBorderDark
import com.example.ui.theme.MetricPillBorderLight
import com.example.ui.theme.MetricPillDark
import com.example.ui.theme.MetricPillLight
import com.example.ui.theme.RainBlueElectric
import com.example.ui.theme.SunAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    viewModel: RainDyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentCity by viewModel.currentCity.collectAsState()
    val weatherState by viewModel.weatherState.collectAsState()
    val cachedWeather by viewModel.cachedWeather.collectAsState()
    val liveTemperature by viewModel.liveTemperature.collectAsState()
    val nextRain by viewModel.nextRainPrediction.collectAsState()
    val nextSun by viewModel.nextSunPrediction.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val lastUpdatedTime by viewModel.lastUpdatedTime.collectAsState()
    val isGpsActive by viewModel.isGpsActive.collectAsState()
    val morphismStyle by viewModel.morphismStyle.collectAsState()
    val climateColor by viewModel.climateThemeColor.collectAsState()
    val weatherArtType by viewModel.weatherArtType.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showLocationSheet by remember { mutableStateOf(false) }
    var showSignInSheet by remember { mutableStateOf(false) }

    val locationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val signInSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Pulsing radar animation dot for live stream
    val pulseScale = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        pulseScale.animateTo(
            targetValue = 1.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshWeather() },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // Location Header & Action Controls (No reload icon, notification bell instead of search)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Location Toggle Pill (Tapping opens the Location Side Drawer / Sheet)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showLocationSheet = true }
                            .popOnTap { showLocationSheet = true }
                            .padding(vertical = 4.dp)
                            .testTag("location_picker_toggle_header")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isGpsActive) AuroraEmerald.copy(alpha = 0.2f) else climateColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isGpsActive) Icons.Default.MyLocation else Icons.Default.LocationOn,
                                contentDescription = "Change Location",
                                tint = if (isGpsActive) AuroraEmerald else climateColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentCity,
                                    color = if (isDark) Color.White else Color(0xFF0F172A),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "Expand Location Picker",
                                    tint = climateColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .scale(pulseScale.value)
                                        .clip(CircleShape)
                                        .background(if (isGpsActive) AuroraEmerald else climateColor)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isGpsActive) "Live GPS Stream • Tap to Change" else "Live Radar • Tap to Change",
                                    color = if (isGpsActive) AuroraEmerald else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Right Side: Notification Icon & Morphism Switcher Pill
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Quick Morphism Toggle (Solid Modern <-> Liquid Glass)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (morphismStyle == MorphismStyle.LIQUID_GLASS) climateColor else (if (isDark) MetricPillDark else MetricPillLight))
                                .border(0.5.dp, if (isDark) Color(0x33FFFFFF) else Color(0x22CBD5E1), RoundedCornerShape(16.dp))
                                .popOnTap { viewModel.toggleMorphismStyle() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("toggle_morphism_style_pill")
                        ) {
                            Text(
                                text = if (morphismStyle == MorphismStyle.LIQUID_GLASS) "Glass 💧" else "Solid 🧊",
                                color = if (morphismStyle == MorphismStyle.LIQUID_GLASS) Color(0xFF0F172A) else (if (isDark) Color.White else Color(0xFF0F172A)),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Notification Icon (Replaces search, as requested by user)
                        IconButton(
                            onClick = {
                                if (userProfile.isGoogleSignedIn) {
                                    viewModel.triggerTestBubbleNotification(context)
                                } else {
                                    showSignInSheet = true
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDark) MetricPillDark else MetricPillLight)
                                .popOnTap {
                                    if (userProfile.isGoogleSignedIn) {
                                        viewModel.triggerTestBubbleNotification(context)
                                    } else {
                                        showSignInSheet = true
                                    }
                                }
                                .testTag("top_bar_notification_bell_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Climate Notifications",
                                tint = if (userProfile.isGoogleSignedIn) climateColor else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Hero Climate Radar Card with Moving Weather Art & Clean Translucent Metrics
            item {
                val curWeather = (weatherState as? WeatherUiState.Success)?.data?.currentWeather
                val hourly = (weatherState as? WeatherUiState.Success)?.data?.hourly

                val temp = liveTemperature ?: curWeather?.temperature ?: cachedWeather?.temperature ?: 22.0
                val weatherCode = curWeather?.weathercode ?: cachedWeather?.weatherCode ?: 0
                val condition = WeatherRepository.mapWeatherCodeToName(weatherCode)
                val rainProb = hourly?.precipitationProbability?.firstOrNull() ?: cachedWeather?.rainProbability ?: 12
                val wind = curWeather?.windspeed ?: cachedWeather?.windSpeed ?: 14.0
                val uv = hourly?.uvIndex?.firstOrNull() ?: cachedWeather?.uvIndex ?: 4.2

                LiquidGlassCard(
                    isDark = isDark,
                    morphismStyle = morphismStyle,
                    accentGlow = climateColor,
                    elevation = 14.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_forecast_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .scale(pulseScale.value)
                                            .clip(CircleShape)
                                            .background(AuroraEmerald)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "LIVE SATELLITE RADAR • $lastUpdatedTime",
                                        color = climateColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                // Live updating temperature figure
                                Text(
                                    text = "${temp.toInt()}°",
                                    color = if (isDark) Color.White else Color(0xFF0F172A),
                                    fontSize = 68.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 68.sp
                                )
                                Text(
                                    text = condition,
                                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Moving Animated Sun / Moon / Rain Canvas Icon
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(climateColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedWeatherArt(
                                    type = weatherArtType,
                                    accentColor = climateColor,
                                    size = 88.dp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Seamless Metrics Row (Eliminates boxy gray background artifacts)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Rain Probability Pill
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isDark) MetricPillDark else MetricPillLight)
                                    .border(0.5.dp, if (isDark) MetricPillBorderDark else MetricPillBorderLight, RoundedCornerShape(18.dp))
                                    .padding(vertical = 12.dp, horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.WaterDrop,
                                            contentDescription = null,
                                            tint = RainBlueElectric,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$rainProb%",
                                            color = if (isDark) Color.White else Color(0xFF0F172A),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Rain Prob",
                                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // UV Index Pill
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isDark) MetricPillDark else MetricPillLight)
                                    .border(0.5.dp, if (isDark) MetricPillBorderDark else MetricPillBorderLight, RoundedCornerShape(18.dp))
                                    .padding(vertical = 12.dp, horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.WbSunny,
                                            contentDescription = null,
                                            tint = SunAmber,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "%.1f".format(uv),
                                            color = if (isDark) Color.White else Color(0xFF0F172A),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "UV Index",
                                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Wind Speed Pill
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isDark) MetricPillDark else MetricPillLight)
                                    .border(0.5.dp, if (isDark) MetricPillBorderDark else MetricPillBorderLight, RoundedCornerShape(18.dp))
                                    .padding(vertical = 12.dp, horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Air,
                                            contentDescription = null,
                                            tint = AuroraEmerald,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${wind.toInt()} km/h",
                                            color = if (isDark) Color.White else Color(0xFF0F172A),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Wind",
                                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Predicted Climate Shift Alert Card
            item {
                val pred = nextRain ?: nextSun
                LiquidGlassCard(
                    isDark = isDark,
                    morphismStyle = morphismStyle,
                    elevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("predictive_climate_alert_card")
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
                                    text = pred?.type?.icon ?: "🌧️",
                                    fontSize = 26.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "PREDICTED WEATHER TRANSITION",
                                        color = climateColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = pred?.title ?: "Next Event: Rain Predicted",
                                        color = if (isDark) Color.White else Color(0xFF0F172A),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Countdown Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(climateColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = pred?.timeDescription ?: "In ~3 hrs",
                                    color = climateColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = pred?.advisory ?: "Precipitation probability rises. Keep an umbrella ready and plan indoor alternatives.",
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ElevatedButton(
                                onClick = {
                                    if (userProfile.isGoogleSignedIn) {
                                        viewModel.triggerTestBubbleNotification(context)
                                    } else {
                                        showSignInSheet = true
                                    }
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = climateColor.copy(alpha = 0.25f),
                                    contentColor = if (isDark) Color.White else Color(0xFF0F172A)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .popOnTap {
                                        if (userProfile.isGoogleSignedIn) {
                                            viewModel.triggerTestBubbleNotification(context)
                                        } else {
                                            showSignInSheet = true
                                        }
                                    }
                                    .testTag("trigger_bubble_notification_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Test Bubble",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            ElevatedButton(
                                onClick = {
                                    pred?.let {
                                        com.example.util.CalendarHelper.addWeatherEventToCalendar(
                                            context = context,
                                            title = it.title,
                                            description = it.advisory,
                                            location = currentCity,
                                            startHourOffset = it.hoursFromNow,
                                            durationHours = 2
                                        )
                                    }
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = climateColor,
                                    contentColor = Color(0xFF0F172A)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .popOnTap {
                                        pred?.let {
                                            com.example.util.CalendarHelper.addWeatherEventToCalendar(
                                                context = context,
                                                title = it.title,
                                                description = it.advisory,
                                                location = currentCity,
                                                startHourOffset = it.hoursFromNow,
                                                durationHours = 2
                                            )
                                        }
                                    }
                                    .testTag("calendar_sync_alert_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Sync Calendar",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Hourly Horizontal Sequence
            item {
                val hourly = (weatherState as? WeatherUiState.Success)?.data?.hourly
                Text(
                    text = "Hourly Micro-Sequence",
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (hourly != null && hourly.time.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val count = minOf(hourly.time.size, hourly.temperature2m.size, 24)
                        for (i in 0 until count) {
                            val rawTime = hourly.time[i]
                            val hourLabel = formatHourBadge(rawTime)
                            val tempI = hourly.temperature2m.getOrNull(i)?.toInt() ?: 20
                            val code = hourly.weatherCode.getOrNull(i) ?: 0
                            val prob = hourly.precipitationProbability.getOrNull(i) ?: 0
                            val iconI = WeatherRepository.mapWeatherCodeToIcon(code)

                            Box(
                                modifier = Modifier
                                    .width(78.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(if (isDark) MetricPillDark else MetricPillLight)
                                    .border(
                                        width = 1.dp,
                                        color = if (prob > 50) AlertRose.copy(alpha = 0.6f) else (if (isDark) MetricPillBorderDark else MetricPillBorderLight),
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .popOnTap {}
                                    .padding(vertical = 14.dp, horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = hourLabel,
                                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = iconI,
                                        fontSize = 24.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "$tempI°",
                                        color = if (isDark) Color.White else Color(0xFF0F172A),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (prob > 0) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "$prob%",
                                            color = if (prob > 50) AlertRose else climateColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = climateColor)
                    }
                }
            }

            // 7-Day Extended Outlook
            item {
                val daily = (weatherState as? WeatherUiState.Success)?.data?.daily
                Text(
                    text = "7-Day Climate Projection",
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (daily != null && daily.time.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        daily.time.take(7).forEachIndexed { index, dateStr ->
                            val code = daily.weatherCode.getOrNull(index) ?: 0
                            val maxT = daily.temperatureMax.getOrNull(index)?.toInt() ?: 24
                            val minT = daily.temperatureMin.getOrNull(index)?.toInt() ?: 14
                            val rainP = daily.precipitationProbabilityMax.getOrNull(index) ?: 10
                            val dayLabel = formatDayName(dateStr, index)
                            val iconD = WeatherRepository.mapWeatherCodeToIcon(code)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isDark) MetricPillDark else MetricPillLight)
                                    .border(0.5.dp, if (isDark) MetricPillBorderDark else MetricPillBorderLight, RoundedCornerShape(20.dp))
                                    .popOnTap {}
                                    .padding(horizontal = 18.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = dayLabel,
                                        color = if (isDark) Color.White else Color(0xFF0F172A),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.width(85.dp)
                                    )
                                    Text(
                                        text = iconD,
                                        fontSize = 22.sp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    if (rainP > 30) {
                                        Text(
                                            text = "$rainP% rain",
                                            color = if (rainP > 60) AlertRose else climateColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$minT°",
                                        color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "$maxT°",
                                        color = if (isDark) Color.White else Color(0xFF0F172A),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Location Side Drawer / Sheet
    if (showLocationSheet) {
        LocationPickerSheet(
            viewModel = viewModel,
            sheetState = locationSheetState,
            onDismiss = { showLocationSheet = false }
        )
    }

    // Google Sign-In Sheet for guests attempting to enable notifications or change settings
    if (showSignInSheet) {
        SignInPromptSheet(
            viewModel = viewModel,
            sheetState = signInSheetState,
            reason = "Sign in with Google to receive real-time climate shift notifications, custom Dynamic Island bubble alerts, and task cloud sync.",
            onDismiss = { showSignInSheet = false }
        )
    }
}

private fun formatHourBadge(isoTime: String): String {
    return try {
        val parts = isoTime.split("T")
        if (parts.size > 1) {
            val h = parts[1].substring(0, 2).toInt()
            val amPm = if (h >= 12) "PM" else "AM"
            val h12 = if (h == 0) 12 else if (h > 12) h - 12 else h
            "$h12 $amPm"
        } else isoTime
    } catch (e: Exception) {
        isoTime
    }
}

private fun formatDayName(isoDate: String, index: Int): String {
    return when (index) {
        0 -> "Today"
        1 -> "Tomorrow"
        else -> {
            try {
                val parts = isoDate.split("-")
                "${parts[1]}/${parts[2]}"
            } catch (e: Exception) {
                "Day ${index + 1}"
            }
        }
    }
}
