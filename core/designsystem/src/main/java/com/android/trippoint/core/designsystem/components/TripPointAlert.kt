package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class AlertVariant {
    Success,
    Warning,
    Error,
    Info
}

@Composable
fun TripPointAlert(
    message: String,
    modifier: Modifier = Modifier,
    variant: AlertVariant = AlertVariant.Info
) {
    val (backgroundColor, contentColor, icon) = when (variant) {
        AlertVariant.Success -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), Icons.Default.CheckCircle)
        AlertVariant.Warning -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), Icons.Default.Warning)
        AlertVariant.Error -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), Icons.Default.Error)
        AlertVariant.Info -> Triple(Color(0xFFDBEAFE), Color(0xFF1E40AF), Icons.Default.Info)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )
    }
}
