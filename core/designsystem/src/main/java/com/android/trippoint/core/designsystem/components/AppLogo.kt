package com.android.trippoint.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.android.trippoint.core.designsystem.R
import com.android.trippoint.core.designsystem.theme.TripPointTheme

@Composable
fun AppLogo(
    modifier: Modifier = Modifier
) {
    val logoSize = TripPointTheme.dimensions.logoSize
    Box(
        modifier = modifier.size(logoSize),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = stringResource(R.string.core_designsystem_app_logo_desc),
            modifier = Modifier.size(logoSize)
        )
    }
}
