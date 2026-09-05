package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TripPointSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    val actionLabel = snackbarData.visuals.actionLabel
    
    Snackbar(
        modifier = modifier.padding(12.dp),
        action = actionLabel?.let {
            {
                TextButton(onClick = { snackbarData.performAction() }) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.onSurface, // Dark background from design
        contentColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = snackbarData.visuals.message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
