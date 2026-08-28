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
import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.core.designsystem.theme.SuccessGreen
import com.android.trippoint.core.designsystem.theme.WarningYellow

@Composable
fun TripStatusChip(
    status: TripStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (status) {
        TripStatus.UPCOMING -> Triple(Color(0xFFDBEAFE), Color(0xFF2563EB), "Upcoming")
        TripStatus.IN_PROGRESS -> Triple(Color(0xFFFEF3C7), WarningYellow, "In Progress")
        TripStatus.COMPLETED -> Triple(Color(0xFFD1FAE5), SuccessGreen, "Completed")
        TripStatus.DRAFT -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), "Draft")
        TripStatus.ARCHIVED -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), "Archived")
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
