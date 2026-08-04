package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

sealed class ButtonVariant {
    object Primary : ButtonVariant()
    object Secondary : ButtonVariant()
    object Text : ButtonVariant()
}

@Composable
fun TripPointButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true
) {
    val commonModifier = modifier
        .fillMaxWidth()
        .height(56.dp)

    when (variant) {
        ButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                modifier = commonModifier,
                enabled = enabled,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(text = text, style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
            }
        }
        ButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = commonModifier,
                enabled = enabled,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = text, style = MaterialTheme.typography.titleMedium)
            }
        }
        ButtonVariant.Text -> {
            TextButton(
                onClick = onClick,
                modifier = commonModifier,
                enabled = enabled
            ) {
                Text(text = text, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
