package com.android.trippoint.core.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * TripPoint Color System (Updated to Module 28).
 */

// Primary Palette (Branded Scale)
val Primary900 = Color(0xFF1A16E0)
val Primary800 = Color(0xFF3720FD)
val Primary700 = Color(0xFF4A3AE0)
val Primary600 = Color(0xFF5B3DF3)
val Primary500 = Color(0xFF7C5CFF) // Main Brand
val Primary400 = Color(0xFFA084FF)
val Primary300 = Color(0xFFC4B5FF)
val Primary200 = Color(0xFFE2DBFF)
val Primary100 = Color(0xFFF0EBFF)
val Primary50 = Color(0xFFF8F5FF)

// Secondary Palette (Section 28.1)
val Secondary900 = Color(0xFF1E3A8A)
val Secondary800 = Color(0xFF1E40AF)
val Secondary700 = Color(0xFF1D4ED8)
val Secondary600 = Color(0xFF2563EB)
val Secondary500 = Color(0xFF3B82F6)
val Secondary400 = Color(0xFF60A5FA)
val Secondary300 = Color(0xFF93C5FD)
val Secondary200 = Color(0xFFBFDBFE)
val Secondary100 = Color(0xFFDBEAFE)
val Secondary50 = Color(0xFFEFF6FF)

// Neutral Palette (Grayscale - Section 28.1)
val Neutral1000 = Color(0xFF000000)
val Neutral900 = Color(0xFF0F172A)
val Neutral800 = Color(0xFF1E293B)
val Neutral700 = Color(0xFF334155)
val Neutral600 = Color(0xFF475569)
val Neutral500 = Color(0xFF64748B)
val Neutral400 = Color(0xFF94A3B8)
val Neutral300 = Color(0xFFCBD5E1)
val Neutral200 = Color(0xFFE2E8F0)
val Neutral100 = Color(0xFFF1F5F9)
val Neutral50 = Color(0xFFF8FAFC)
val Neutral0 = Color(0xFFFFFFFF)

// Semantic Palette
val SuccessGreen = Color(0xFF22C55E)
val SuccessLight = Color(0xFFDCFCE7)
val InfoBlue = Color(0xFF0EA5E9)
val InfoBlueLight = Color(0xFFDBEAFE)
val WarningYellow = Color(0xFFF59E0B)
val WarningLight = Color(0xFFFEF3C7)
val ErrorRed = Color(0xFFEF4444)
val ErrorLight = Color(0xFFFEE2E2)
val SemanticNeutral = Neutral500
val SemanticPurple = Color(0xFF8B5CF6)
val SemanticTeal = Color(0xFF14B8A6)
val SemanticPink = Color(0xFFEC4899)

// Surface & Background Roles (Section 28.2)
val SurfaceColor = Color(0xFFFFFFFF)
val SurfaceVariantColor = Color(0xFFF8FAFC)
val BackgroundColor = Color(0xFFF5F7FA)
val InverseSurfaceColor = Color(0xFF0F172A)

// Chart Colors (Section 28.10)
val Chart1 = Color(0xFF7C5CFF)
val Chart2 = Color(0xFF0EA5E9)
val Chart3 = Color(0xFF22C55E)
val Chart4 = Color(0xFFF59E0B)
val Chart5 = Color(0xFFEF4444)
val Chart6 = Color(0xFF8B5CF6)

// Gradients (Section 26.8)
val PrimaryGradient = Brush.linearGradient(
    colors = listOf(Primary500, Primary600)
)
val SkyGradient = Brush.linearGradient(
    colors = listOf(InfoBlue, Primary500)
)
val SunsetGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFEF3C7), Color(0xFFF59E0B)) // Warning range
)
val ForestGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFDCFCE7), Color(0xFF22C55E)) // Success range
)
val OceanGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFBFDBFE), Color(0xFF3B82F6)) // Secondary range
)
val BerryGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFEE2E2), Color(0xFFEF4444)) // Error range
)

// Legacy compatibility aliases
val PrimaryBlue = Primary500
val PrimaryBlueLight = Primary400
val Accent = WarningYellow
val Background = BackgroundColor
val Surface = SurfaceColor
val TextPrimary = Neutral900
val TextSecondary = Neutral500
val Border = Neutral200

// Dark Mode Palette
val DarkBackground = Color(0xFF0B1220)
val DarkSurface = Color(0xFF111827)
val DarkSurfaceElevated = Color(0xFF1F2937)
val DarkBorder = Color(0xFF1F2937)
val DarkNeutral300 = Color(0xFF94A3B8)
val DarkNeutral500 = Color(0xFF64748B)
val DarkNeutral700 = Color(0xFF334155)
