package com.android.trippoint.core.designsystem.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class TripPointDimensions(
    val spacingExtraSmall: Dp = 4.dp,
    val spacingSmall: Dp = 8.dp,
    val spacingMedium: Dp = 12.dp,
    val spacingLarge: Dp = 16.dp,
    val spacingExtraLarge: Dp = 24.dp,
    val spacingXXLarge: Dp = 32.dp,
    val spacingXXXLarge: Dp = 40.dp,
    val spacingHuge: Dp = 48.dp,
    val spacingExHuge: Dp = 64.dp,
    val spacingGigantic: Dp = 80.dp,
    val spacingEnormous: Dp = 96.dp,

    // Component Sizes
    val logoSize: Dp = 96.dp,
    val illustrationHeight: Dp = 320.dp,
    val loaderSize: Dp = 32.dp,
    val iconSizeLarge: Dp = 100.dp
)

val LocalDimensions = compositionLocalOf { TripPointDimensions() }
