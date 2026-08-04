package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.trippoint.core.designsystem.R

@Composable
fun SplashIllustration(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.ic_splash_illustrator),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillWidth
    )
}
