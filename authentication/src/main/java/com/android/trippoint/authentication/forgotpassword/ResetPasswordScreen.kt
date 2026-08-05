package com.android.trippoint.authentication.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.R
import com.android.trippoint.core.designsystem.components.FullscreenStatusView
import com.android.trippoint.core.designsystem.components.PasswordStrengthIndicator
import com.android.trippoint.core.designsystem.components.SplashIllustration
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTextField

@Composable
fun ResetPasswordRoute(
    onNavigateToLogin: () -> Unit,
    viewModel: ResetPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ResetPasswordContract.Effect.NavigateToLogin -> onNavigateToLogin()
                is ResetPasswordContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    ResetPasswordScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun ResetPasswordScreen(
    uiState: ResetPasswordContract.State,
    onIntent: (ResetPasswordContract.Intent) -> Unit
) {
    when {
        uiState.isSuccess -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_reset_password_success_title),
                subtitle = stringResource(R.string.auth_reset_password_success_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_success,
                actionText = stringResource(R.string.auth_forgot_password_back_to_login),
                onActionClick = { onIntent(ResetPasswordContract.Intent.ResetClicked) }
            )
        }
        uiState.isLinkExpired -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_reset_password_link_expired_title),
                subtitle = stringResource(R.string.auth_reset_password_link_expired_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_error,
                actionText = stringResource(R.string.auth_reset_password_request_new),
                onActionClick = { onIntent(ResetPasswordContract.Intent.RequestNewLinkClicked) }
            )
        }
        uiState.offlineError -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_error_offline_title),
                subtitle = stringResource(R.string.auth_error_offline_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_no_network,
                actionText = stringResource(R.string.auth_retry),
                onActionClick = { onIntent(ResetPasswordContract.Intent.ResetClicked) }
            )
        }
        uiState.serverError -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_error_server_title),
                subtitle = stringResource(R.string.auth_error_server_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_error,
                actionText = stringResource(R.string.auth_retry),
                onActionClick = { onIntent(ResetPasswordContract.Intent.ResetClicked) }
            )
        }
        else -> {
            ResetPasswordContent(uiState, onIntent)
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

@Suppress("LongMethod")
@Composable
private fun ResetPasswordContent(
    uiState: ResetPasswordContract.State,
    onIntent: (ResetPasswordContract.Intent) -> Unit
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
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_ready,
                modifier = Modifier.height(200.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.auth_reset_password_title),
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.auth_reset_password_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            TripPointTextField(
                value = uiState.password,
                onValueChange = { onIntent(ResetPasswordContract.Intent.PasswordChanged(it)) },
                label = stringResource(R.string.auth_register_password_label),
                placeholder = stringResource(R.string.auth_register_password_placeholder),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    val icon = if (uiState.isPasswordVisible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    }
                    IconButton(onClick = {
                        onIntent(ResetPasswordContract.Intent.TogglePasswordVisibility)
                    }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                },
                visualTransformation = if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError?.let { stringResource(it) }
            )

            if (uiState.password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                PasswordStrengthIndicator(
                    progress = uiState.passwordStrength.progress,
                    color = uiState.passwordStrength.color,
                    label = stringResource(uiState.passwordStrength.labelResId)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            TripPointTextField(
                value = uiState.confirmPassword,
                onValueChange = { onIntent(ResetPasswordContract.Intent.ConfirmPasswordChanged(it)) },
                label = stringResource(R.string.auth_register_confirm_password_label),
                placeholder = stringResource(R.string.auth_register_confirm_password_placeholder),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    val icon = if (uiState.isConfirmPasswordVisible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    }
                    IconButton(onClick = {
                        onIntent(ResetPasswordContract.Intent.ToggleConfirmPasswordVisibility)
                    }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                },
                visualTransformation = if (uiState.isConfirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                isError = uiState.confirmPasswordError != null,
                errorMessage = uiState.confirmPasswordError?.let { stringResource(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            TripPointButton(
                text = stringResource(R.string.auth_reset_password_button),
                onClick = { onIntent(ResetPasswordContract.Intent.ResetClicked) },
                enabled = uiState.password.isNotEmpty() &&
                    uiState.confirmPassword.isNotEmpty() && !uiState.isLoading
            )
        }
    }
}
