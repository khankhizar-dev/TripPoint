package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

/**
 * An Icon with a count badge overlay (Section 24.7).
 */
@Composable
fun TripPointIconBadge(
    icon: ImageVector,
    count: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Box(modifier = modifier.padding(4.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        if (count > 0) {
            TripPointCountBadge(
                count = count,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = (-4).dp, end = (-4).dp)
            )
        }
    }
}
