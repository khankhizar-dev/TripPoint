package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.trippoint.core.designsystem.theme.Primary500

enum class AvatarSize(val size: Dp) {
    XS(24.dp),
    S(32.dp),
    M(48.dp),
    L(64.dp),
    XL(80.dp)
}

@Composable
fun TripPointAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.M,
    initials: String? = null,
    badgeColor: Color? = null
) {
    Box(
        modifier = modifier.size(size.size)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (initials != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Primary500),
                    contentAlignment = Alignment.Center
                ) {
                    val textStyle = when (size) {
                        AvatarSize.XS -> MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp)
                        AvatarSize.S -> MaterialTheme.typography.labelSmall
                        else -> MaterialTheme.typography.titleMedium
                    }
                    Text(
                        text = initials,
                        style = textStyle,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        
        if (badgeColor != null) {
            val badgeSize = size.size * 0.3f
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(badgeColor)
                    .border(2.dp, Color.White, CircleShape)
            )
        }
    }
}

@Composable
fun TripPointGroupAvatar(
    images: List<String?>,
    modifier: Modifier = Modifier,
    extraCount: Int = 0,
    size: AvatarSize = AvatarSize.S
) {
    Row(modifier = modifier) {
        images.take(3).forEachIndexed { index, url ->
            TripPointAvatar(
                imageUrl = url,
                size = size,
                modifier = Modifier
                    .offset(x = (index * (-12)).dp)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
        if (extraCount > 0) {
            val overlapOffset = (minOf(images.size, 3) * (-12)).dp
            Box(
                modifier = Modifier
                    .offset(x = overlapOffset)
                    .size(size.size)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$extraCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
