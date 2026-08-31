package com.android.trippoint.core.designsystem.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.theme.TripPointTheme

/**
 * Functional states and expressions for the AI Assistant (Section 26.6).
 */
enum class AiState {
    // Functional States
    IDLE,
    THINKING,
    EXPLAINING,
    SUCCESS,
    CELEBRATION,
    
    // Expression Variations
    HAPPY,
    NEUTRAL,
    SURPRISED,
    SAD,
    EXCITED
}

@Composable
fun AiAssistantCharacter(
    state: AiState,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val illustrations = TripPointTheme.illustrations
    val imageResId = when (state) {
        AiState.IDLE -> illustrations.aiIdle
        AiState.THINKING -> illustrations.aiThinking
        AiState.EXPLAINING -> illustrations.aiExplaining
        AiState.SUCCESS -> illustrations.aiSuccess
        AiState.CELEBRATION -> illustrations.aiCelebration
        AiState.HAPPY -> illustrations.aiHappy
        AiState.NEUTRAL -> illustrations.aiNeutral
        AiState.SURPRISED -> illustrations.aiSurprised
        AiState.SAD -> illustrations.aiSad
        AiState.EXCITED -> illustrations.aiExcited
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (state == AiState.THINKING) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        SplashIllustration(
            imageResId = imageResId,
            modifier = Modifier.size(size)
        )
    }
}
