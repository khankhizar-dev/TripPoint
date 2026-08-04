package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.trippoint.core.designsystem.R
import com.android.trippoint.core.designsystem.theme.TripPointTheme

@Composable
fun LoadingView(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.core_designsystem_loading)
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoadingIndicator()
        Spacer(modifier = Modifier.height(TripPointTheme.dimensions.spacingLarge))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
