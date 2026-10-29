package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LiquidGlassDarkBorder
import com.example.ui.theme.LiquidGlassDarkFill
import com.example.ui.theme.LiquidGlassLightBorder
import com.example.ui.theme.LiquidGlassLightFill
import com.example.ui.theme.SolidModernDarkBorder
import com.example.ui.theme.SolidModernDarkSurface
import com.example.ui.theme.SolidModernLightBorder
import com.example.ui.theme.SolidModernLightSurface

enum class MorphismStyle {
    SOLID_MODERN,  // Clean, crisp solid neumorphic surfaces
    LIQUID_GLASS   // iPhone Liquid Glass: Frosted specular translucent glass
}

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    isDark: Boolean = true,
    morphismStyle: MorphismStyle = MorphismStyle.SOLID_MODERN,
    accentGlow: Color? = null,
    customFill: Brush? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isLiquidGlass = morphismStyle == MorphismStyle.LIQUID_GLASS

    // Background fill
    val backgroundBrush = customFill ?: if (isLiquidGlass) {
        if (isDark) LiquidGlassDarkFill else LiquidGlassLightFill
    } else {
        if (isDark) {
            Brush.verticalGradient(
                listOf(
                    SolidModernDarkSurface,
                    Color(0xFF0F1728)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    SolidModernLightSurface,
                    Color(0xFFFFFFFF)
                )
            )
        }
    }

    // Border stroke
    val borderBrush = if (isLiquidGlass) {
        if (isDark) LiquidGlassDarkBorder else LiquidGlassLightBorder
    } else {
        Brush.linearGradient(
            listOf(
                if (isDark) SolidModernDarkBorder else SolidModernLightBorder,
                if (isDark) Color(0x33475569) else Color(0x33CBD5E1)
            )
        )
    }

    // Shadow colors
    val shadowAmbient = if (isLiquidGlass) {
        if (isDark) Color(0x40000000) else Color(0x0F0F172A)
    } else {
        if (isDark) Color(0x55000000) else Color(0x0A0F172A)
    }

    val shadowSpot = if (isLiquidGlass) {
        accentGlow?.copy(alpha = if (isDark) 0.35f else 0.18f) ?: (if (isDark) Color(0x33F5A158) else Color(0x1AF5A158))
    } else {
        accentGlow?.copy(alpha = if (isDark) 0.2f else 0.08f) ?: Color.Transparent
    }

    val cardModifier = modifier
        .shadow(
            elevation = if (isDark) elevation else (elevation / 2),
            shape = shape,
            ambientColor = shadowAmbient,
            spotColor = shadowSpot
        )
        .clip(shape)
        .background(backgroundBrush)
        .border(width = borderWidth, brush = borderBrush, shape = shape)
        .then(
            if (onClick != null) {
                Modifier.popOnTap(onClick = onClick)
            } else {
                Modifier
            }
        )

    Box(
        modifier = cardModifier,
        content = content
    )
}
