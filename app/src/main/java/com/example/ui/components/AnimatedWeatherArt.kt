package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.FreezeGlacialBlue
import com.example.ui.theme.RainBlueElectric
import com.example.ui.theme.RainDyApricot
import kotlin.math.cos
import kotlin.math.sin

enum class WeatherArtType {
    SUN,
    MOON,
    RAIN,
    SNOW,
    CLOUD
}

@Composable
fun AnimatedWeatherArt(
    type: WeatherArtType,
    accentColor: Color = RainDyApricot,
    size: Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "weather_art_motion")

    // Rotation for sun rays
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sun_rotation"
    )

    // Breathing pulse for solar corona / moon glow
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corona_pulse"
    )

    // Rain droplet offset
    val rainDropY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "raindrop_fall"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h / 2f)

            when (type) {
                WeatherArtType.SUN -> {
                    val coreRadius = (w * 0.28f) * pulse

                    // Outer Solar Corona Aura
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.35f),
                                accentColor.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = w * 0.5f
                        ),
                        center = center,
                        radius = w * 0.5f
                    )

                    // Rotating Modern Solar Beams (8 rays)
                    val rayCount = 8
                    val rayInner = w * 0.36f
                    val rayOuter = w * 0.46f

                    for (i in 0 until rayCount) {
                        val angleDeg = rotation + (i * (360f / rayCount))
                        val angleRad = Math.toRadians(angleDeg.toDouble())
                        val cosA = cos(angleRad).toFloat()
                        val sinA = sin(angleRad).toFloat()

                        val start = Offset(center.x + cosA * rayInner, center.y + sinA * rayInner)
                        val end = Offset(center.x + cosA * rayOuter, center.y + sinA * rayOuter)

                        drawLine(
                            color = accentColor.copy(alpha = 0.85f),
                            start = start,
                            end = end,
                            strokeWidth = (w * 0.055f),
                            cap = StrokeCap.Round
                        )
                    }

                    // Sun Core with rich glowing gradient
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.95f),
                                accentColor,
                                accentColor.copy(alpha = 0.9f)
                            ),
                            center = Offset(center.x - coreRadius * 0.25f, center.y - coreRadius * 0.25f),
                            radius = coreRadius * 1.2f
                        ),
                        center = center,
                        radius = coreRadius
                    )
                }

                WeatherArtType.MOON -> {
                    // Celestial Crescent Moon with Star Sparkles
                    val moonRadius = w * 0.32f
                    val glowRadius = w * 0.48f * pulse

                    // Night Aura
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x33818CF8),
                                Color(0x1538BDF8),
                                Color.Transparent
                            )
                        ),
                        center = center,
                        radius = glowRadius
                    )

                    // Crescent Path
                    val path = Path().apply {
                        addArc(
                            oval = androidx.compose.ui.geometry.Rect(
                                center.x - moonRadius,
                                center.y - moonRadius,
                                center.x + moonRadius,
                                center.y + moonRadius
                            ),
                            startAngleDegrees = -90f,
                            sweepAngleDegrees = 270f
                        )
                    }
                    drawCircle(
                        color = Color(0xFFE2E8F0),
                        center = center,
                        radius = moonRadius
                    )
                    // Inner shadow cutout
                    drawCircle(
                        color = Color(0xFF0F172A),
                        center = Offset(center.x + moonRadius * 0.45f, center.y - moonRadius * 0.25f),
                        radius = moonRadius * 0.82f
                    )

                    // Twinkling Star
                    val starCenter = Offset(w * 0.28f, h * 0.26f)
                    val starSize = w * 0.07f * pulse
                    drawLine(
                        color = Color(0xFFFBBF24),
                        start = Offset(starCenter.x - starSize, starCenter.y),
                        end = Offset(starCenter.x + starSize, starCenter.y),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color(0xFFFBBF24),
                        start = Offset(starCenter.x, starCenter.y - starSize),
                        end = Offset(starCenter.x, starCenter.y + starSize),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                }

                WeatherArtType.RAIN -> {
                    // Modern Fluid Cloud + Falling Rain Droplets
                    val cloudCenter = Offset(center.x, h * 0.38f)
                    val cloudColor = Color(0xFF94A3B8)

                    // Cloud Puffs
                    drawCircle(color = cloudColor, radius = w * 0.22f, center = cloudCenter)
                    drawCircle(color = cloudColor.copy(alpha = 0.8f), radius = w * 0.16f, center = Offset(cloudCenter.x - w * 0.2f, cloudCenter.y + h * 0.04f))
                    drawCircle(color = cloudColor.copy(alpha = 0.8f), radius = w * 0.18f, center = Offset(cloudCenter.x + w * 0.18f, cloudCenter.y + h * 0.04f))

                    // Falling rain lines with animation
                    val drop1Y = (h * 0.60f) + (rainDropY * (h * 0.32f))
                    val drop2Y = (h * 0.60f) + (((rainDropY + 0.5f) % 1f) * (h * 0.32f))

                    drawLine(
                        color = RainBlueElectric,
                        start = Offset(w * 0.35f, drop1Y),
                        end = Offset(w * 0.32f, drop1Y + (h * 0.12f)),
                        strokeWidth = (w * 0.04f),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = RainBlueElectric,
                        start = Offset(w * 0.52f, drop2Y),
                        end = Offset(w * 0.49f, drop2Y + (h * 0.12f)),
                        strokeWidth = (w * 0.04f),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = RainBlueElectric,
                        start = Offset(w * 0.68f, drop1Y),
                        end = Offset(w * 0.65f, drop1Y + (h * 0.12f)),
                        strokeWidth = (w * 0.04f),
                        cap = StrokeCap.Round
                    )
                }

                WeatherArtType.SNOW -> {
                    // Ice Flake Crystal
                    val flakeCenter = center
                    val flakeRadius = w * 0.32f * pulse
                    for (i in 0 until 6) {
                        val angle = Math.toRadians((rotation + i * 60f).toDouble())
                        val cosA = cos(angle).toFloat()
                        val sinA = sin(angle).toFloat()
                        drawLine(
                            color = FreezeGlacialBlue,
                            start = flakeCenter,
                            end = Offset(flakeCenter.x + cosA * flakeRadius, flakeCenter.y + sinA * flakeRadius),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.08f,
                        center = flakeCenter
                    )
                }

                WeatherArtType.CLOUD -> {
                    // Drifting Overcast Cloud
                    val cX = center.x + (pulse - 1f) * (w * 0.1f)
                    val cY = center.y
                    drawCircle(color = Color(0xFF64748B), radius = w * 0.26f, center = Offset(cX, cY))
                    drawCircle(color = Color(0xFF94A3B8), radius = w * 0.20f, center = Offset(cX - w * 0.22f, cY + h * 0.05f))
                    drawCircle(color = Color(0xFF94A3B8), radius = w * 0.22f, center = Offset(cX + w * 0.20f, cY + h * 0.05f))
                }
            }
        }
    }
}
