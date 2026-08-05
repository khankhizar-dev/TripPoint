package com.android.trippoint.authentication.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.R
import com.android.trippoint.core.designsystem.components.FullscreenStatusView
import com.android.trippoint.core.designsystem.components.OtpInput
import com.android.trippoint.core.designsystem.components.SplashIllustration
import com.android.trippoint.core.designsystem.components.TripPointButton

@Composable
fun OtpRoute(
    email: String,
    onNavigateToHome: () -> Unit,
    viewModel: OtpViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OtpContract.Effect.NavigateToHome -> onNavigateToHome()
                is OtpContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    OtpScreen(
        uiState = uiState.copy(email = email),
        onIntent = viewModel::onIntent
    )
}

@Composable
fun OtpScreen(
    uiState: OtpContract.State,
    onIntent: (OtpContract.Intent) -> Unit
) {
    when {
        uiState.isSuccess -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_otp_success_title),
                subtitle = stringResource(R.string.auth_otp_success_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_success
            )
        }
        uiState.offlineError -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_error_offline_title),
                subtitle = stringResource(R.string.auth_error_offline_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_no_network,
                actionText = stringResource(R.string.auth_retry),
                onActionClick = { onIntent(OtpContract.Intent.VerifyClicked) }
            )
        }
        uiState.serverError -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_error_server_title),
                subtitle = stringResource(R.string.auth_error_server_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_error,
                actionText = stringResource(R.string.auth_retry),
                onActionClick = { onIntent(OtpContract.Intent.VerifyClicked) }
            )
        }
        else -> {
            OtpContent(uiState, onIntent)
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun OtpContent(
    uiState: OtpContract.State,
    onIntent: (OtpContract.Intent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            SplashIllustration(
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_get_started,
                modifier = Modifier.height(200.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.auth_otp_title),
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.auth_otp_subtitle, uiState.email),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            OtpInput(
                otpText = uiState.otp,
                onOtpTextChange = { otp, isComplete ->
                    onIntent(OtpContract.Intent.OtpChanged(otp))
                    if (isComplete) onIntent(OtpContract.Intent.VerifyClicked)
                }
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(uiState.error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TripPointButton(
                text = stringResource(R.string.auth_otp_verify_button),
                onClick = { onIntent(OtpContract.Intent.VerifyClicked) },
                enabled = uiState.otp.length == 6 && !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.auth_otp_resend_prompt),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = { onIntent(OtpContract.Intent.ResendClicked) },
                    enabled = uiState.resendTimer == 0
                ) {
                    val resendText = if (uiState.resendTimer > 0) {
                        stringResource(R.string.auth_otp_resend_timer, uiState.resendTimer)
                    } else {
                        stringResource(R.string.auth_otp_resend_button)
                    }
                    Text(
                        text = resendText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.resendTimer > 0) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }
    }
}
