package com.android.trippoint.core.designsystem.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * TripPoint Dimension System (Updated to Module 28).
 */
data class TripPointDimensions(
    // 4pt Spacing Grid (Section 28.1)
    val spacing0: Dp = 0.dp,
    val spacing1: Dp = 4.dp,
    val spacing2: Dp = 8.dp,
    val spacing3: Dp = 12.dp,
    val spacing4: Dp = 16.dp,
    val spacing5: Dp = 20.dp,
    val spacing6: Dp = 24.dp,
    val spacing7: Dp = 28.dp,
    val spacing8: Dp = 32.dp,
    val spacing9: Dp = 36.dp,
    val spacing10: Dp = 40.dp,
    val spacing12: Dp = 48.dp,
    val spacing16: Dp = 64.dp,
    val spacing20: Dp = 80.dp,
    val spacing24: Dp = 96.dp,
    val spacing32: Dp = 128.dp,

    // Border Radius Scale (Section 28.3)
    val radius0: Dp = 0.dp,
    val radius1: Dp = 4.dp,
    val radius2: Dp = 6.dp,
    val radius3: Dp = 8.dp,
    val radius4: Dp = 12.dp,
    val radius5: Dp = 16.dp,
    val radius6: Dp = 24.dp,
    val radius7: Dp = 9999.dp,

    // Breakpoints (Section 28.4)
    val breakpointXs: Dp = 0.dp,
    val breakpointSm: Dp = 576.dp,
    val breakpointMd: Dp = 768.dp,
    val breakpointLg: Dp = 1024.dp,
    val breakpointXl: Dp = 1280.dp,
    val breakpoint2xl: Dp = 1536.dp,

    // Component Sizes
    val avatarSm: Dp = 32.dp,
    val avatarMd: Dp = 48.dp,
    val avatarLg: Dp = 64.dp,
    val iconSize: Dp = 24.dp,
    val logoSize: Dp = 96.dp,
    val illustrationHeight: Dp = 320.dp,
    val loaderSize: Dp = 32.dp,
    val buttonHeightLarge: Dp = 56.dp,
    val buttonHeightMedium: Dp = 48.dp,
    val buttonHeightSmall: Dp = 36.dp,
    
    // Elevation Scale (Section 28.2)
    val elevation0: Dp = 0.dp,
    val elevation1: Dp = 2.dp,
    val elevation2: Dp = 4.dp,
    val elevation3: Dp = 8.dp,
    val elevation4: Dp = 16.dp,
    val elevation5: Dp = 24.dp,

    // Legacy compatibility aliases
    val spacingXs: Dp = spacing1,
    val spacingSm: Dp = spacing2,
    val spacingMd: Dp = spacing4,
    val spacingLg: Dp = spacing6,
    val spacingXl: Dp = spacing8,
    val spacing2xl: Dp = spacing12,
    val spacing3xl: Dp = spacing16,
    val radiusXs: Dp = radius1,
    val radiusSm: Dp = radius2,
    val radiusMd: Dp = radius3,
    val radiusLg: Dp = radius4,
    val radiusXl: Dp = radius5,
    val radius2xl: Dp = radius6,
    val radiusFull: Dp = radius7,
    val spacingExtraSmall: Dp = spacingXs,
    val spacingSmall: Dp = spacingSm,
    val spacingMedium: Dp = spacing3,
    val spacingLarge: Dp = spacing4,
    val spacingExtraLarge: Dp = spacingLg,
    val spacingXXLarge: Dp = spacingXl,
    val spacingXXXLarge: Dp = spacing10,
    val spacingHuge: Dp = spacing12,
    val spacingExHuge: Dp = spacing16,
    val spacingGigantic: Dp = 80.dp,
    val spacingEnormous: Dp = 96.dp
)

val LocalDimensions = compositionLocalOf { TripPointDimensions() }
