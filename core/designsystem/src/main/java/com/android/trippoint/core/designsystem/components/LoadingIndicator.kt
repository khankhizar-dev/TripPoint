package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.theme.TripPointTheme

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier.size(TripPointTheme.dimensions.loaderSize),
        strokeWidth = 3.dp,
        color = MaterialTheme.colorScheme.primary
    )
}
