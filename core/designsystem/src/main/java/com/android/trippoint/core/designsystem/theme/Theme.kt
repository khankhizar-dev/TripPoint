package com.android.trippoint.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary500,
    onPrimary = Color.White,
    primaryContainer = Primary600,
    onPrimaryContainer = Color.White,
    secondary = DarkNeutral700,
    onSecondary = Color.White,
    tertiary = SemanticPurple,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = Neutral50,
    surface = DarkSurface,
    onSurface = Neutral50,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkNeutral300,
    error = ErrorRed,
    onError = Color.White,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Primary500,
    onPrimary = Color.White,
    primaryContainer = Primary200,
    onPrimaryContainer = Primary600,
    secondary = Neutral500,
    onSecondary = Color.White,
    tertiary = SemanticPurple,
    onTertiary = Color.White,
    background = Neutral50,
    onBackground = Neutral900,
    surface = Color.White,
    onSurface = Neutral900,
    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral500,
    error = ErrorRed,
    onError = Color.White,
    outline = Neutral300
)

@Suppress("LongParameterList")
@Composable
fun TripPointTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    dimensions: TripPointDimensions = TripPointDimensions(),
    motion: TripPointMotion = TripPointMotion(),
    brand: TripPointBrand = TripPointBrand(),
    dataViz: TripPointDataViz = TripPointDataViz(),
    illustrations: TripPointIllustrations = TripPointIllustrations(),
    patterns: TripPointPatterns = TripPointPatterns(),
    tone: TripPointTone = TripPointTone(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalDimensions provides dimensions,
        LocalMotion provides motion,
        LocalBrand provides brand,
        LocalDataViz provides dataViz,
        LocalIllustrations provides illustrations,
        LocalPatterns provides patterns,
        LocalTone provides tone
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object TripPointTheme {
    val dimensions: TripPointDimensions
        @Composable
        get() = LocalDimensions.current
        
    val motion: TripPointMotion
        @Composable
        get() = LocalMotion.current
        
    val brand: TripPointBrand
        @Composable
        get() = LocalBrand.current
        
    val dataViz: TripPointDataViz
        @Composable
        get() = LocalDataViz.current

    val illustrations: TripPointIllustrations
        @Composable
        get() = LocalIllustrations.current

    val patterns: TripPointPatterns
        @Composable
        get() = LocalPatterns.current

    val tone: TripPointTone
        @Composable
        get() = LocalTone.current
        
    val typography: androidx.compose.material3.Typography
        @Composable
        get() = MaterialTheme.typography
        
    val colorScheme: androidx.compose.material3.ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme
}
