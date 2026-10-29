package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RainDyViewModel
import com.example.ui.components.RainDySocialLogo
import com.example.ui.components.popOnTap
import com.example.ui.theme.RainDyApricot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInPromptSheet(
    viewModel: RainDyViewModel,
    sheetState: SheetState,
    reason: String = "Sign in with Google to enable climate push notifications, custom bubble alerts, and task cloud sync.",
    onDismiss: () -> Unit
) {
    val isDark by viewModel.isDarkMode.collectAsState()
    val climateColor by viewModel.climateThemeColor.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0x33FFFFFF) else Color(0x33000000))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            // Social-Media RainDy Logo
            RainDySocialLogo(
                size = 72.dp,
                accentColor = climateColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sign In Required",
                color = if (isDark) Color.White else Color(0xFF0F172A),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = reason,
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Unlocked Features List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDark) Color(0x22FFFFFF) else Color(0x0C0F172A))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UnlockFeatureRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "Live Climate Shift Notifications",
                    desc = "Get notified before rain starts or freezing weather hits",
                    isDark = isDark,
                    tint = climateColor
                )
                UnlockFeatureRow(
                    icon = Icons.Default.CalendarToday,
                    title = "Google Calendar Weather Sync",
                    desc = "Smart scheduling for outdoor running, cycling & tasks",
                    isDark = isDark,
                    tint = Color(0xFF38BDF8)
                )
                UnlockFeatureRow(
                    icon = Icons.Default.Sync,
                    title = "Multi-device Cloud Task Sync",
                    desc = "Seamless offline caching with automatic cloud restore",
                    isDark = isDark,
                    tint = Color(0xFF10B981)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google Sign-In Button
            ElevatedButton(
                onClick = {
                    viewModel.signInWithGoogle()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .popOnTap {
                        viewModel.signInWithGoogle()
                        onDismiss()
                    }
                    .testTag("google_sign_in_sheet_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = if (isDark) Color.White else Color(0xFF0F172A),
                    contentColor = if (isDark) Color(0xFF0F172A) else Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    GoogleColoredIcon(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sign in with Google",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = onDismiss) {
                Text(
                    text = "Continue as Guest (Read-Only)",
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun UnlockFeatureRow(
    icon: ImageVector,
    title: String,
    desc: String,
    isDark: Boolean,
    tint: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = if (isDark) Color.White else Color(0xFF0F172A),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = desc,
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun GoogleColoredIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)

        // Draw 4 quadrant rings of Google logo
        drawCircle(color = Color(0xFFEA4335), radius = w * 0.45f, center = center)
        drawCircle(color = Color(0xFF4285F4), radius = w * 0.35f, center = center)
        drawCircle(color = Color(0xFFFBBC05), radius = w * 0.25f, center = center)
        drawCircle(color = Color(0xFF34A853), radius = w * 0.15f, center = center)
    }
}
