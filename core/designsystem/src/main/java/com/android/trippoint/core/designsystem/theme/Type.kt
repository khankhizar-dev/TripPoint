package com.android.trippoint.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * TripPoint Typography System (Super-Sync from M16, M23, M24, M28).
 * This ensures no sizes are removed while adopting the latest M28 naming as primary.
 */

// Define explicit base styles to ensure absolute fidelity
private val Display1Base = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 48.sp,
    lineHeight = 56.sp
)

private val Display2Base = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 36.sp,
    lineHeight = 44.sp
)

private val Display3Base = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 32.sp,
    lineHeight = 40.sp
)

private val HeadlineLargeBase = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 32.sp
)

private val HeadlineMediumBase = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 28.sp
)

private val HeadlineSmallBase = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    lineHeight = 24.sp
)

private val BodyLargeBase = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp
)

private val BodyMediumBase = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp
)

private val BodySmallBase = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp
)

private val LabelLargeBase = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp
)

private val LabelMediumBase = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp
)

private val LabelSmallBase = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 14.sp
)

val Typography = Typography(
    displayLarge = Display1Base,
    displayMedium = Display2Base,
    displaySmall = Display3Base,
    headlineLarge = HeadlineLargeBase,
    headlineMedium = HeadlineMediumBase,
    headlineSmall = HeadlineSmallBase,
    titleLarge = HeadlineSmallBase, // Mapping titleLarge to 18sp
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = BodyLargeBase,
    bodyMedium = BodyMediumBase,
    bodySmall = BodySmallBase,
    labelLarge = LabelLargeBase,
    labelMedium = LabelMediumBase,
    labelSmall = LabelSmallBase
)

// DESIGN SYSTEM SEMANTIC ALIASES (Non-Destructive)

// M28 Aliases (Latest)
val TextStyle.H1 get() = Typography.displaySmall // M28 H1 is 32sp
val TextStyle.H2 get() = Typography.headlineLarge // M28 H2 is 24sp
val TextStyle.H3 get() = Typography.headlineMedium // M28 H3 is 20sp
val TextStyle.Body1 get() = Typography.bodyLarge
val TextStyle.Body2 get() = Typography.bodyMedium
val TextStyle.Caption get() = Typography.labelLarge

// M23 Aliases (Premium sizes)
val TextStyle.Display1 get() = Typography.displayLarge // 48sp
val TextStyle.Display2 get() = Typography.displayMedium // 36sp
val TextStyle.Display3 get() = Typography.displaySmall // 32sp
val TextStyle.Heading1 get() = Typography.headlineLarge // 24sp
val TextStyle.Heading2 get() = Typography.headlineMedium // 20sp
val TextStyle.Heading3 get() = Typography.bodyLarge // 16sp
val TextStyle.Subtitle1 get() = Typography.titleMedium // 16sp
val TextStyle.Subtitle2 get() = Typography.titleSmall // 14sp
val TextStyle.Overline get() = Typography.labelSmall // 11sp

// M16 Aliases (Scale names)
val TextStyle.StyleDisplayLarge get() = Typography.displaySmall // 32sp
val TextStyle.StyleDisplayMedium get() = Typography.headlineLarge // 24sp
val TextStyle.StyleHeadlineLarge get() = Typography.headlineMedium // 20sp
val TextStyle.StyleHeadlineMedium get() = Typography.headlineSmall // 18sp
val TextStyle.StyleTitleLarge get() = Typography.titleMedium // 16sp
val TextStyle.StyleTitleMedium get() = Typography.titleSmall // 14sp
val TextStyle.StyleBodyLarge get() = Typography.bodyLarge // 16sp
val TextStyle.StyleBodyMedium get() = Typography.bodyMedium // 14sp
val TextStyle.StyleBodySmall get() = Typography.bodySmall // 12sp
val TextStyle.StyleLabel get() = Typography.labelLarge // 14sp
