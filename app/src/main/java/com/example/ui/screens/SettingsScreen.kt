package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.AuroraEmerald
import com.example.ui.theme.RainBlueElectric
import com.example.ui.theme.RainCyan
import com.example.ui.theme.SunAmber

@Composable
fun SettingsScreen(
    viewModel: RainDyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark by viewModel.isDarkMode.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val notificationPrefs by viewModel.notificationPrefs.collectAsState()
    val morphismStyle by viewModel.morphismStyle.collectAsState()
    val climateColor by viewModel.climateThemeColor.collectAsState()

    var rainThreshold by remember { mutableStateOf(notificationPrefs.rainAlertThresholdPercent.toFloat()) }
    var soundVibrate by remember { mutableStateOf(notificationPrefs.enableSoundAndVibrate) }
    var autoCalendar by remember { mutableStateOf(notificationPrefs.autoCalendarSync) }

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
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = climateColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Preferences & System",
                        color = if (isDark) Color.White else Color(0xFF0F172A),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Morphism design, accounts & live location tracking",
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Morphism Style Selector Card
        item {
            LiquidGlassCard(
                isDark = isDark,
                morphismStyle = morphismStyle,
                accentGlow = climateColor,
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth().testTag("morphism_settings_card")
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
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = climateColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Morphism Style Mode",
                                    color = if (isDark) Color.White else Color(0xFF0F172A),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (morphismStyle == MorphismStyle.SOLID_MODERN) "Active: Solid Modern (Crisp)" else "Active: iPhone Liquid Glass",
                                    color = climateColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Switch(
                            checked = morphismStyle == MorphismStyle.LIQUID_GLASS,
                            onCheckedChange = { viewModel.toggleMorphismStyle() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = climateColor,
                                checkedTrackColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.testTag("morphism_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "• Solid Modern (Default): Minimalist high-contrast surfaces, solid elevation, clean spacious typography.\n" +
                               "• iPhone Liquid Glass: Ultra-frosted specular translucence, prismatic refraction borders, and glowing chromatic backdrops.",
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Account Profile & Google Sign-In with Automatic Location Tracking
        item {
            LiquidGlassCard(
                isDark = isDark,
                morphismStyle = morphismStyle,
                accentGlow = climateColor,
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth().testTag("profile_card")
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
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (userProfile.isGoogleSignedIn) AuroraEmerald else climateColor.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userProfile.avatarInitials,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = userProfile.displayName,
                                    color = if (isDark) Color.White else Color(0xFF0F172A),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = userProfile.email,
                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (userProfile.isGoogleSignedIn) Color(0x3310B981) else climateColor.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (userProfile.isGoogleSignedIn) "Google Synced" else "Offline Guest",
                                color = if (userProfile.isGoogleSignedIn) AuroraEmerald else climateColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Once you sign in, RainDy automatically resolves your physical device GPS coordinates to track your live local micro-climate forecast.",
                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ElevatedButton(
                        onClick = { viewModel.toggleGoogleSignIn(context) },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = if (userProfile.isGoogleSignedIn) Color(0x33FFFFFF) else climateColor,
                            contentColor = if (userProfile.isGoogleSignedIn) Color.White else Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .popOnTap { viewModel.toggleGoogleSignIn(context) }
                            .testTag("google_signin_button")
                    ) {
                        Icon(
                            imageVector = if (userProfile.isGoogleSignedIn) Icons.Default.AccountCircle else Icons.Default.MyLocation,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (userProfile.isGoogleSignedIn) "Sign out of Google" else "Sign in with Google & Track Location",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Appearance Mode (Dark vs Light)
        item {
            LiquidGlassCard(
                isDark = isDark,
                morphismStyle = morphismStyle,
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth().testTag("theme_toggle_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = climateColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (isDark) "Night Horizon Dark Theme" else "Daylight Crystal Light Theme",
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Dynamic climate color accent: ${if (climateColor == RainCyan) "Freezing Blue" else "Apricot #f5a158"}",
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Switch(
                        checked = isDark,
                        onCheckedChange = { viewModel.toggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = climateColor,
                            checkedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("dark_mode_switch")
                    )
                }
            }
        }

        // Auto-Notification Preferences & Sensitivity
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
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = climateColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Automatic Climate Alert Triggers",
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Rain Probability Notification Threshold: ${rainThreshold.toInt()}%",
                        color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Slider(
                        value = rainThreshold,
                        onValueChange = {
                            rainThreshold = it
                            viewModel.updatePrefs(notificationPrefs.copy(rainAlertThresholdPercent = it.toInt()))
                        },
                        valueRange = 20f..80f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = climateColor,
                            activeTrackColor = climateColor.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("rain_threshold_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Auto Sync Smart Windows to Calendar",
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Suggests dry outdoor activity slots",
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = autoCalendar,
                            onCheckedChange = {
                                autoCalendar = it
                                viewModel.updatePrefs(notificationPrefs.copy(autoCalendarSync = it))
                            }
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
