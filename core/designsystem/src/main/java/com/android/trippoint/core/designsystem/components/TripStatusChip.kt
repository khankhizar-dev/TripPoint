package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.common.model.TripStatus
import com.android.trippoint.core.designsystem.theme.InfoBlue
import com.android.trippoint.core.designsystem.theme.Neutral500
import com.android.trippoint.core.designsystem.theme.SuccessGreen
import com.android.trippoint.core.designsystem.theme.WarningYellow

@Composable
fun TripStatusChip(
    status: TripStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (status) {
        TripStatus.UPCOMING -> Triple(InfoBlue.copy(alpha = 0.1f), InfoBlue, "Upcoming")
        TripStatus.IN_PROGRESS -> Triple(WarningYellow.copy(alpha = 0.1f), WarningYellow, "In Progress")
        TripStatus.COMPLETED -> Triple(SuccessGreen.copy(alpha = 0.1f), SuccessGreen, "Completed")
        TripStatus.DRAFT -> Triple(Neutral500.copy(alpha = 0.1f), Neutral500, "Draft")
        TripStatus.ARCHIVED -> Triple(Neutral500.copy(alpha = 0.1f), Neutral500, "Archived")
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(100.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
