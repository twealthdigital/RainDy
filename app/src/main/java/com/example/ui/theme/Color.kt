package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary RainDy Brand Colors
val RainDyApricot = Color(0xFFF5A158) // User requested brand color #f5a158
val RainDyApricotGlow = Color(0x66F5A158)
val RainDyApricotDark = Color(0xFFE28532)

// Climate-Adaptive Temperature States
val FreezeGlacialBlue = Color(0xFF38BDF8) // For freezing <= 5°C
val FreezeGlacierGlow = Color(0x6638BDF8)
val FreezeIceSurface = Color(0xFF0F1E38)

val TemperateTeal = Color(0xFF14B8A6) // For mild/cool 6°C - 19°C

val SummerAmber = Color(0xFFF5A158) // For pleasant/summer 20°C - 28°C (#f5a158)

val ExtremeHeatOrange = Color(0xFFFF5E1A) // For hot > 28°C (solar fire)
val ExtremeHeatGlow = Color(0x66FF5E1A)

// Backgrounds & Surfaces (Solid Modern Morphism)
val SolidModernDarkBg = Color(0xFF080D1A)
val SolidModernDarkSurface = Color(0xFF131D31)
val SolidModernDarkSurfaceElevated = Color(0xFF1C2740)
val SolidModernDarkBorder = Color(0xFF263554)

val SolidModernLightBg = Color(0xFFF8FAFC)
val SolidModernLightSurface = Color(0xFFFFFFFF)
val SolidModernLightSurfaceElevated = Color(0xFFF1F5F9)
val SolidModernLightBorder = Color(0xFFE2E8F0)

// Liquid Glass Morphism (iPhone Specular Translucence)
val LiquidGlassDarkFill = Brush.verticalGradient(
    listOf(
        Color(0x3320304D),
        Color(0x1C131D31)
    )
)

val LiquidGlassLightFill = Brush.verticalGradient(
    listOf(
        Color(0xF2FFFFFF),
        Color(0xD9FFFFFF)
    )
)

val LiquidGlassDarkBorder = Brush.linearGradient(
    listOf(
        Color(0x70FFFFFF),
        Color(0x1AFFFFFF),
        Color(0x0DFFFFFF),
        Color(0x40F5A158)
    )
)

val LiquidGlassLightBorder = Brush.linearGradient(
    listOf(
        Color(0xFFFFFFFF),
        Color(0x66FFFFFF),
        Color(0x40E2E8F0),
        Color(0x33F5A158)
    )
)

// Metric Pills (Seamless floating pills without heavy opaque box styling)
val MetricPillDark = Color(0x14FFFFFF)
val MetricPillLight = Color(0x0C0F172A)
val MetricPillBorderDark = Color(0x1AFFFFFF)
val MetricPillBorderLight = Color(0x120F172A)

// Accent Vibrants
val RainCyan = Color(0xFF00E5FF)
val RainBlueElectric = Color(0xFF38BDF8)
val SunAmber = Color(0xFFFBBF24)
val StormPurple = Color(0xFFA855F7)
val AuroraEmerald = Color(0xFF10B981)
val AlertRose = Color(0xFFF43F5E)

// Backward compatibility aliases
val RainDyDeepSpace = SolidModernDarkBg
val RainDyMidnightSurface = SolidModernDarkSurface
val GlassBorderDark = LiquidGlassDarkBorder
val GlassBorderLight = LiquidGlassLightBorder
val GlassFillDark = LiquidGlassDarkFill
val GlassFillLight = LiquidGlassLightFill
