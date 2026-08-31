package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class IconButtonVariant {
    Standard,
    Filled,
    Destructive
}

@Composable
fun TripPointIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: IconButtonVariant = IconButtonVariant.Standard,
    size: Dp = 40.dp,
    enabled: Boolean = true
) {
    val containerColor = when (variant) {
        IconButtonVariant.Standard -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        IconButtonVariant.Filled -> MaterialTheme.colorScheme.primary
        IconButtonVariant.Destructive -> MaterialTheme.colorScheme.error
    }
    
    val contentColor = when (variant) {
        IconButtonVariant.Standard -> MaterialTheme.colorScheme.primary
        IconButtonVariant.Filled -> Color.White
        IconButtonVariant.Destructive -> Color.White
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (enabled) containerColor else containerColor.copy(alpha = 0.3f))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(size * 0.6f),
            tint = if (enabled) contentColor else contentColor.copy(alpha = 0.38f)
        )
    }
}
