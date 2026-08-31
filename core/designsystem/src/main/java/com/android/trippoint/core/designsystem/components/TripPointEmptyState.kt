package com.android.trippoint.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.trippoint.core.designsystem.R

@Composable
fun TripPointEmptyState(
    title: String,
    subtitle: String,
    @DrawableRes imageResId: Int,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SplashIllustration(
            imageResId = imageResId,
            modifier = Modifier.size(200.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(32.dp))
            TripPointButton(
                text = actionText,
                onClick = onActionClick,
                size = ButtonSize.Medium,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
    }
}

@Composable
fun TripPointOfflineState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    TripPointEmptyState(
        title = "You're offline",
        subtitle = "Check your connection and try again.",
        imageResId = R.drawable.illustration_offline,
        actionText = "Retry",
        onActionClick = onRetry,
        modifier = modifier
    )
}

@Composable
fun TripPointNoResultsState(
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "No results found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Try adjusting your search or filters.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        TripPointButton(
            text = "Clear Filters",
            onClick = onClearFilters,
            variant = ButtonVariant.Secondary,
            size = ButtonSize.Medium,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
}
