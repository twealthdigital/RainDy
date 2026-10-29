package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RainCyan
import com.example.ui.theme.RainDyApricot

/**
 * Facebook / Social Media Style App Logo for RainDy:
 * A clean, minimalist, iconic squircle emblem featuring a bold geometric "R"
 * seamlessly fused with a liquid glass climate droplet and solar ambient rim.
 */
@Composable
fun RainDySocialLogo(
    size: Dp = 96.dp,
    animated: Boolean = true,
    accentColor: Color = RainDyApricot,
    modifier: Modifier = Modifier
) {
    val shimmerOffset = remember { Animatable(-0.5f) }

    LaunchedEffect(animated) {
        if (animated) {
            shimmerOffset.animateTo(
                targetValue = 1.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    val cornerRadius = size * 0.26f

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color(0x40000000),
                spotColor = accentColor.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF141E33),
                        Color(0xFF0F172A),
                        Color(0xFF070B14)
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x99FFFFFF),
                        accentColor.copy(alpha = 0.8f),
                        Color(0x2238BDF8),
                        Color(0x10FFFFFF)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.76f)) {
            val w = this.size.width
            val h = this.size.height

            // 1. Ambient Sun / Climate Aura in top right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.6f),
                        accentColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.78f, h * 0.25f),
                    radius = w * 0.42f
                )
            )

            // 2. Glowing Liquid Rain Droplet Contour
            val dropPath = Path().apply {
                moveTo(w * 0.72f, h * 0.14f)
                cubicTo(
                    w * 0.88f, h * 0.32f,
                    w * 0.90f, h * 0.50f,
                    w * 0.72f, h * 0.58f
                )
                cubicTo(
                    w * 0.56f, h * 0.52f,
                    w * 0.56f, h * 0.32f,
                    w * 0.72f, h * 0.14f
                )
                close()
            }
            drawPath(
                path = dropPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        RainCyan.copy(alpha = 0.85f),
                        Color(0xFF38BDF8).copy(alpha = 0.7f),
                        accentColor.copy(alpha = 0.5f)
                    )
                )
            )

            // 3. Bold, Modern Social Emblem "R" Stem & Curves
            // Vertical bar of "R"
            val stemWidth = w * 0.18f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color.White, Color(0xFFE2E8F0))
                ),
                topLeft = Offset(w * 0.18f, h * 0.14f),
                size = Size(stemWidth, h * 0.72f),
                cornerRadius = CornerRadius(stemWidth * 0.35f, stemWidth * 0.35f)
            )

            // Top loop of "R"
            val loopPath = Path().apply {
                moveTo(w * 0.32f, h * 0.14f)
                lineTo(w * 0.54f, h * 0.14f)
                cubicTo(
                    w * 0.76f, h * 0.14f,
                    w * 0.76f, h * 0.46f,
                    w * 0.54f, h * 0.46f
                )
                lineTo(w * 0.32f, h * 0.46f)
            }
            drawPath(
                path = loopPath,
                brush = Brush.horizontalGradient(
                    listOf(Color.White, accentColor, Color.White)
                ),
                style = Stroke(
                    width = stemWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Diagonal kick leg of "R"
            val legPath = Path().apply {
                moveTo(w * 0.44f, h * 0.44f)
                cubicTo(
                    w * 0.56f, h * 0.56f,
                    w * 0.64f, h * 0.72f,
                    w * 0.76f, h * 0.86f
                )
            }
            drawPath(
                path = legPath,
                brush = Brush.linearGradient(
                    listOf(accentColor, Color.White)
                ),
                style = Stroke(
                    width = stemWidth * 0.95f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 4. Subtle Prismatic Shimmer across the emblem
            if (animated) {
                val shimmerX = w * shimmerOffset.value
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x55FFFFFF),
                            Color.Transparent
                        ),
                        start = Offset(shimmerX - w * 0.25f, 0f),
                        end = Offset(shimmerX + w * 0.25f, h)
                    ),
                    start = Offset(shimmerX - w * 0.25f, 0f),
                    end = Offset(shimmerX + w * 0.25f, h),
                    strokeWidth = w * 0.3f
                )
            }
        }
    }
}
