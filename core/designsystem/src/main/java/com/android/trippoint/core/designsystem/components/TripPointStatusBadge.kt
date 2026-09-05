package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class StatusVariant {
    Confirmed,
    Pending,
    Cancelled,
    Refunded,
    Draft
}

@Composable
fun TripPointStatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: StatusVariant = StatusVariant.Confirmed
) {
    val (dotColor, backgroundColor, textColor) = when (variant) {
        StatusVariant.Confirmed -> Triple(Color(0xFF22C55E), Color(0xFFDCFCE7), Color(0xFF166534))
        StatusVariant.Pending -> Triple(Color(0xFFF59E0B), Color(0xFFFEF3C7), Color(0xFF92400E))
        StatusVariant.Cancelled -> Triple(Color(0xFFEF4444), Color(0xFFFEE2E2), Color(0xFF991B1B))
        StatusVariant.Refunded -> Triple(Color(0xFF0EA5E9), Color(0xFFDBEAFE), Color(0xFF1E40AF))
        StatusVariant.Draft -> Triple(Color(0xFF64748B), Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
