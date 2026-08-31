package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.common.model.Priority

enum class BadgeVariant {
    Default,
    Success,
    Warning,
    Error,
    Info,
    Offer,
    New
}

@Composable
fun TripPointBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default
) {
    val (backgroundColor, textColor) = when (variant) {
        BadgeVariant.Default -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        BadgeVariant.Success -> Color(0xFFDCFCE7) to Color(0xFF166534)
        BadgeVariant.Warning -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        BadgeVariant.Error -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
        BadgeVariant.Info -> Color(0xFFDBEAFE) to Color(0xFF1E40AF)
        BadgeVariant.Offer -> Color(0xFFDCFCE7) to Color(0xFF166534) // ~20% OFF style
        BadgeVariant.New -> MaterialTheme.colorScheme.primary to Color.White
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(100.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
fun TripPointPriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val variant = when (priority) {
        Priority.LOW -> BadgeVariant.Success
        Priority.MEDIUM -> BadgeVariant.Warning
        Priority.HIGH -> BadgeVariant.Error
    }
    TripPointBadge(
        text = priority.name,
        variant = variant,
        modifier = modifier
    )
}
