package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.trippoint.core.designsystem.R
import com.android.trippoint.core.designsystem.theme.TripPointTheme

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
