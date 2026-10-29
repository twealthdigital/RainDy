package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DynamicIslandBubble
import com.example.ui.components.MorphismStyle
import com.example.ui.components.popOnTap
import com.example.ui.screens.ClimateTrendsScreen
import com.example.ui.screens.ForecastScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.RainBlueElectric
import com.example.ui.theme.RainCyan
import com.example.ui.theme.RainDyTheme
import com.example.ui.theme.SolidModernDarkBorder
import com.example.ui.theme.SolidModernDarkSurface
import com.example.ui.theme.SolidModernLightBorder
import com.example.ui.theme.SolidModernLightSurface
import com.example.util.CalendarHelper

enum class RainDyTab(val title: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
    FORECAST("Radar", Icons.Filled.CloudQueue, Icons.Outlined.CloudQueue),
    TRENDS("Trends", Icons.Filled.Public, Icons.Outlined.Public),
    PLANNER("Planner", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    TASKS("Tasks", Icons.Filled.TaskAlt, Icons.Outlined.TaskAlt),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun RainDyApp(viewModel: RainDyViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    val isDark by viewModel.isDarkMode.collectAsState()
    val morphismStyle by viewModel.morphismStyle.collectAsState()
    val climateColor by viewModel.climateThemeColor.collectAsState()
    var currentTab by remember { mutableStateOf(RainDyTab.FORECAST) }

    val activeBubbleAlert by viewModel.activeBubbleAlert.collectAsState()
    val isBubbleVisible by viewModel.isBubbleVisible.collectAsState()
    val context = LocalContext.current

    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    RainDyTheme(darkTheme = isDark) {
        if (showSplash) {
            SplashScreen(onSplashFinished = { showSplash = false })
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isDark) {
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF070C18),
                                    Color(0xFF0F172A),
                                    Color(0xFF0A0F1D)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFFFFFF),
                                    Color(0xFFFBFDFF),
                                    Color(0xFFF1F5F9)
                                )
                            )
                        }
                    )
            ) {
                Scaffold(
                    containerColor = Color.Transparent,
                    contentWindowInsets = WindowInsets.safeDrawing,
                    bottomBar = {
                        ModernBottomBar(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it },
                            isDark = isDark,
                            morphismStyle = morphismStyle,
                            climateColor = climateColor,
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Screen transition animation with spring physics
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                                (slideInHorizontally(
                                    initialOffsetX = { direction * it / 2 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                ) + fadeIn(animationSpec = tween(250))).togetherWith(
                                    slideOutHorizontally(
                                        targetOffsetX = { -direction * it / 2 },
                                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                    ) + fadeOut(animationSpec = tween(200))
                                )
                            },
                            label = "tab_content_transition"
                        ) { tab ->
                            when (tab) {
                                RainDyTab.FORECAST -> ForecastScreen(viewModel = viewModel)
                                RainDyTab.TRENDS -> ClimateTrendsScreen(viewModel = viewModel)
                                RainDyTab.PLANNER -> PlannerScreen(viewModel = viewModel)
                                RainDyTab.TASKS -> TasksScreen(viewModel = viewModel)
                                RainDyTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            }
                        }

                        // Floating Dynamic Island Bubble Notification
                        DynamicIslandBubble(
                            prediction = activeBubbleAlert,
                            visible = isBubbleVisible,
                            accentColor = climateColor,
                            onDismiss = { viewModel.dismissBubble() },
                            onAddToCalendar = {
                                activeBubbleAlert?.let { pred ->
                                    CalendarHelper.addWeatherEventToCalendar(
                                        context = context,
                                        title = pred.title,
                                        description = "${pred.advisory}\nPredicted: ${pred.formattedTime}",
                                        location = viewModel.currentCity.value,
                                        startHourOffset = pred.hoursFromNow,
                                        durationHours = 2
                                    )
                                }
                                viewModel.dismissBubble()
                            },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernBottomBar(
    currentTab: RainDyTab,
    onTabSelected: (RainDyTab) -> Unit,
    isDark: Boolean,
    morphismStyle: MorphismStyle,
    climateColor: Color,
    modifier: Modifier = Modifier
) {
    val barShape = RoundedCornerShape(32.dp)
    val isLiquidGlass = morphismStyle == MorphismStyle.LIQUID_GLASS

    val backgroundBrush = if (isLiquidGlass) {
        if (isDark) {
            Brush.verticalGradient(listOf(Color(0xE61E293B), Color(0xFA0F172A)))
        } else {
            Brush.verticalGradient(listOf(Color(0xF0FFFFFF), Color(0xE6F8FAFC)))
        }
    } else {
        if (isDark) {
            Brush.verticalGradient(listOf(SolidModernDarkSurface, Color(0xFF101726)))
        } else {
            Brush.verticalGradient(listOf(SolidModernLightSurface, Color(0xFFF1F4F9)))
        }
    }

    val borderBrush = if (isLiquidGlass) {
        Brush.linearGradient(
            listOf(
                if (isDark) Color(0x66FFFFFF) else Color(0xAAFFFFFF),
                climateColor.copy(alpha = 0.4f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                if (isDark) SolidModernDarkBorder else SolidModernLightBorder,
                if (isDark) Color(0x33475569) else Color(0x33CBD5E1)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .shadow(
                elevation = if (isLiquidGlass) 20.dp else 10.dp,
                shape = barShape,
                ambientColor = if (isDark) Color(0x66000000) else Color(0x1A0F172A),
                spotColor = if (isLiquidGlass) climateColor.copy(alpha = 0.4f) else Color.Transparent
            )
            .clip(barShape)
            .background(backgroundBrush)
            .border(width = 1.dp, brush = borderBrush, shape = barShape)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .testTag("modern_bottom_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RainDyTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                val icon = if (isSelected) tab.activeIcon else tab.inactiveIcon
                val iconTint = if (isSelected) {
                    climateColor
                } else {
                    if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .popOnTap { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("tab_${tab.name.lowercase()}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    climateColor.copy(alpha = 0.2f)
                                } else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.title,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = tab.title,
                        color = iconTint,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
