package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class VerticalStep(
    val title: String,
    val subtitle: String? = null,
    val isCompleted: Boolean = false,
    val isActive: Boolean = false,
    val isError: Boolean = false
)

@Composable
fun TripPointVerticalStepper(
    steps: List<VerticalStep>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        steps.forEachIndexed { index, step ->
            VerticalStepItem(
                step = step,
                index = index + 1,
                isLast = index == steps.size - 1
            )
        }
    }
}

@Composable
private fun VerticalStepItem(
    step: VerticalStep,
    index: Int,
    isLast: Boolean
) {
    Row {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StepIndicator(step = step, index = index)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp) // Adjusted height to be more compact like M24
                        .background(
                            if (step.isCompleted) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.padding(top = 4.dp)) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (step.isActive) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    step.isError -> MaterialTheme.colorScheme.error
                    step.isActive -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            if (step.subtitle != null) {
                Text(
                    text = step.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StepIndicator(step: VerticalStep, index: Int) {
    val backgroundColor = when {
        step.isError -> MaterialTheme.colorScheme.error
        step.isCompleted -> MaterialTheme.colorScheme.primary
        step.isActive -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = when {
        step.isError || step.isCompleted || step.isActive -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (step.isCompleted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = contentColor
            )
        } else {
            Text(
                text = index.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
