package com.android.trippoint.authentication.session

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.android.trippoint.authentication.R
import com.android.trippoint.core.designsystem.components.FullscreenStatusView

@Composable
fun SessionExpiredScreen(
    onLoginAgain: () -> Unit
) {
    FullscreenStatusView(
        title = stringResource(R.string.auth_session_expired_title),
        subtitle = stringResource(R.string.auth_session_expired_subtitle),
        imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_error,
        actionText = stringResource(R.string.auth_session_login_again),
        onActionClick = onLoginAgain
    )
}
