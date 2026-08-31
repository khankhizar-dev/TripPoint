package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun TripPointImage(
    model: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    contentScale: ContentScale = ContentScale.Crop,
    badgeColor: Color? = null,
    badgeIcon: ImageVector? = Icons.Default.Check,
    badgeSize: Dp = 20.dp
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = Modifier
                .matchParentSize()
                .clip(shape),
            contentScale = contentScale
        )
        
        if (badgeColor != null) {
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .offset(x = 4.dp, y = 4.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(badgeColor)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (badgeIcon != null) {
                    Icon(
                        imageVector = badgeIcon,
                        contentDescription = null,
                        modifier = Modifier.size(badgeSize * 0.6f),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
