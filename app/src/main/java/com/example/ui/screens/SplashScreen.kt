package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RainDySocialLogo
import com.example.ui.components.popOnTap
import com.example.ui.theme.AuroraEmerald
import com.example.ui.theme.RainBlueElectric
import com.example.ui.theme.RainCyan
import com.example.ui.theme.RainDyApricot
import com.example.ui.theme.SunAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class SplashStep {
    INITIAL_LOADING,
    ONBOARDING,
    TRANSITION_RELOAD
}

data class OnboardingSlide(
    val title: String,
    val subtitle: String,
    val highlight: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var step by remember { mutableStateOf(SplashStep.INITIAL_LOADING) }
    var currentSlideIndex by remember { mutableIntStateOf(0) }
    val initialProgress = remember { Animatable(0f) }
    val reloadProgress = remember { Animatable(0f) }

    val slides = listOf(
        OnboardingSlide(
            title = "Live Climate Shift Alerts",
            subtitle = "Get custom dynamic bubble notifications on your phone with predictive times before rain, freezing cold, or heat strikes.",
            highlight = "Predictive Bubble Alerts",
            icon = Icons.Default.NotificationsActive,
            accentColor = RainDyApricot
        ),
        OnboardingSlide(
            title = "Liquid Glass & Thermal Themes",
            subtitle = "Switch smoothly between iPhone Liquid Glass and Solid Modern, with full color adaptation to freezing blue, summer apricot, and hot sun.",
            highlight = "Adaptive Morphism",
            icon = Icons.Default.AutoAwesome,
            accentColor = Color(0xFF38BDF8)
        ),
        OnboardingSlide(
            title = "Live Location & Smart Sync",
            subtitle = "Reads your exact live town and country automatically, with worldwide map search, offline task manager, and calendar sync.",
            highlight = "Offline-First Planning",
            icon = Icons.Default.CloudQueue,
            accentColor = Color(0xFF10B981)
        )
    )

    // Initial Loading Animation (~1.6 seconds)
    LaunchedEffect(Unit) {
        initialProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
        delay(100)
        step = SplashStep.ONBOARDING
    }

    // Auto-advancing Onboarding Pages (moves to next page every 2.8s automatically)
    LaunchedEffect(step, currentSlideIndex) {
        if (step == SplashStep.ONBOARDING) {
            delay(2800)
            if (currentSlideIndex < 2) {
                currentSlideIndex++
            }
        }
    }

    // Secondary Quick Reload Animation when user finishes onboarding (~600ms)
    LaunchedEffect(step) {
        if (step == SplashStep.TRANSITION_RELOAD) {
            reloadProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = LinearEasing)
            )
            onSplashFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070B14),
                        Color(0xFF0F172A),
                        Color(0xFF080D19)
                    )
                )
            )
            .testTag("splash_screen_root"),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background glow
        Canvas(modifier = Modifier.size(360.dp)) {
            val curColor = if (step == SplashStep.ONBOARDING) slides[currentSlideIndex].accentColor else RainDyApricot
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        curColor.copy(alpha = 0.22f),
                        curColor.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                ),
                radius = size.width / 2f
            )
        }

        when (step) {
            SplashStep.INITIAL_LOADING -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    RainDySocialLogo(
                        size = 110.dp,
                        accentColor = RainDyApricot
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "RainDy",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Live Climate Intelligence • iPhone Glass",
                        color = RainDyApricot,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Fast Loading Bar
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(initialProgress.value)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(RainDyApricot, RainCyan, Color(0xFF38BDF8))
                                    )
                                )
                        )
                    }
                }
            }

            SplashStep.ONBOARDING -> {
                val curSlide = slides[currentSlideIndex]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Bar: Skip button & Step indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RainDySocialLogo(
                                size = 32.dp,
                                animated = false,
                                accentColor = curSlide.accentColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "RainDy",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TextButton(
                            onClick = { step = SplashStep.TRANSITION_RELOAD },
                            modifier = Modifier.testTag("skip_onboarding_btn")
                        ) {
                            Text(
                                text = "Skip",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Slide Content
                    AnimatedContent(
                        targetState = currentSlideIndex,
                        transitionSpec = {
                            (slideInHorizontally { width -> width / 2 } + fadeIn())
                                .togetherWith(slideOutHorizontally { width -> -width / 2 } + fadeOut())
                        },
                        label = "onboarding_slide"
                    ) { index ->
                        val slide = slides[index]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            // Icon Orb with Ambient Glass Rim
                            Box(
                                modifier = Modifier
                                    .size(112.dp)
                                    .shadow(
                                        elevation = 20.dp,
                                        shape = CircleShape,
                                        ambientColor = Color(0x33000000),
                                        spotColor = slide.accentColor.copy(alpha = 0.5f)
                                    )
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0xFF1E293B),
                                                Color(0xFF0F172A)
                                            )
                                        )
                                    )
                                    .border(2.dp, slide.accentColor.copy(alpha = 0.7f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = slide.icon,
                                    contentDescription = null,
                                    tint = slide.accentColor,
                                    modifier = Modifier.size(52.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(slide.accentColor.copy(alpha = 0.18f))
                                    .border(1.dp, slide.accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = slide.highlight.uppercase(),
                                    color = slide.accentColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = slide.title,
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = slide.subtitle,
                                color = Color(0xFFCBD5E1),
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Bottom: Auto-advancing Pill Indicators and Action Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 3 Smooth Auto-advancing Progress Pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 0..2) {
                                val isSelected = currentSlideIndex == i
                                val pillWidth by animateFloatAsState(
                                    targetValue = if (isSelected) 36f else 10f,
                                    animationSpec = tween(350, easing = FastOutSlowInEasing),
                                    label = "pill_width"
                                )
                                Box(
                                    modifier = Modifier
                                        .height(6.dp)
                                        .width(pillWidth.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) curSlide.accentColor else Color(0x33FFFFFF)
                                        )
                                        .clickable { currentSlideIndex = i }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Next / Get Started Button
                        ElevatedButton(
                            onClick = {
                                if (currentSlideIndex < 2) {
                                    currentSlideIndex++
                                } else {
                                    step = SplashStep.TRANSITION_RELOAD
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .popOnTap {
                                    if (currentSlideIndex < 2) {
                                        currentSlideIndex++
                                    } else {
                                        step = SplashStep.TRANSITION_RELOAD
                                    }
                                }
                                .testTag("onboarding_next_btn"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = curSlide.accentColor,
                                contentColor = Color(0xFF0F172A)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (currentSlideIndex == 2) "Get Started 🚀" else "Next",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            SplashStep.TRANSITION_RELOAD -> {
                // Quick, Snappy Logo Transition (~600ms) into main app
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    RainDySocialLogo(
                        size = 110.dp,
                        animated = true,
                        accentColor = RainDyApricot
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Loading Radar...",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(3.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(reloadProgress.value)
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(RainDyApricot)
                        )
                    }
                }
            }
        }
    }
}
