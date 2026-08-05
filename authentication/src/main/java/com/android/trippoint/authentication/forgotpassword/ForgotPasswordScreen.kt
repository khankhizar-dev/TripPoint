package com.android.trippoint.authentication.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.R
import com.android.trippoint.core.designsystem.components.FullscreenStatusView
import com.android.trippoint.core.designsystem.components.SplashIllustration
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTextField

@Composable
fun ForgotPasswordRoute(
    onNavigateBack: () -> Unit,
    onNavigateToOtp: (String) -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ForgotPasswordContract.Effect.NavigateToLogin -> onNavigateBack()
                is ForgotPasswordContract.Effect.NavigateToOtp -> onNavigateToOtp(effect.email)
                is ForgotPasswordContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    ForgotPasswordScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ForgotPasswordScreen(
    uiState: ForgotPasswordContract.State,
    onIntent: (ForgotPasswordContract.Intent) -> Unit
) {
    when {
        uiState.isSuccess -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_forgot_password_success_title),
                subtitle = stringResource(R.string.auth_forgot_password_success_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_reset_link,
                actionText = stringResource(R.string.auth_forgot_password_open_email),
                onActionClick = { /* Open email app */ }
            )
        }
        uiState.offlineError -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_error_offline_title),
                subtitle = stringResource(R.string.auth_error_offline_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_no_network,
                actionText = stringResource(R.string.auth_retry),
                onActionClick = { onIntent(ForgotPasswordContract.Intent.SendLinkClicked) }
            )
        }
        uiState.serverError -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_error_server_title),
                subtitle = stringResource(R.string.auth_error_server_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_error,
                actionText = stringResource(R.string.auth_retry),
                onActionClick = { onIntent(ForgotPasswordContract.Intent.SendLinkClicked) }
            )
        }
        else -> {
            ForgotPasswordContent(uiState, onIntent)
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
private fun ForgotPasswordContent(
    uiState: ForgotPasswordContract.State,
    onIntent: (ForgotPasswordContract.Intent) -> Unit
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            SplashIllustration(
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_get_started,
                modifier = Modifier.height(200.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.auth_forgot_password_title),
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.auth_forgot_password_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            TripPointTextField(
                value = uiState.email,
                onValueChange = { onIntent(ForgotPasswordContract.Intent.EmailChanged(it)) },
                label = stringResource(R.string.auth_register_email_label),
                placeholder = stringResource(R.string.auth_register_email_placeholder),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError?.let { stringResource(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(32.dp))

            TripPointButton(
                text = stringResource(R.string.auth_forgot_password_button),
                onClick = { onIntent(ForgotPasswordContract.Intent.SendLinkClicked) },
                enabled = uiState.email.isNotEmpty() && !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            TripPointButton(
                text = stringResource(R.string.auth_forgot_password_back_to_login),
                onClick = { onIntent(ForgotPasswordContract.Intent.BackToLoginClicked) },
                variant = com.android.trippoint.core.designsystem.components.ButtonVariant.Text
            )
        }
    }
}
