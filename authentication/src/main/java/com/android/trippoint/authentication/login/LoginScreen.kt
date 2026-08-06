package com.android.trippoint.authentication.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.R
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.designsystem.components.ButtonVariant
import com.android.trippoint.core.designsystem.components.FullscreenStatusView
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTextField

@Composable
fun LoginRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(PreferencesManager(context)) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginContract.Effect.NavigateToHome -> onNavigateToHome()
                is LoginContract.Effect.NavigateToSignUp -> onNavigateToSignUp()
                is LoginContract.Effect.NavigateToForgotPassword -> onForgotPassword()
                is LoginContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun LoginScreen(
    uiState: LoginContract.State,
    onIntent: (LoginContract.Intent) -> Unit
) {
    when {
        uiState.isSuccess -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_login_success_title),
                subtitle = stringResource(R.string.auth_login_success_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_success
            )
        }
        uiState.offlineError -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_error_offline_title),
                subtitle = stringResource(R.string.auth_error_offline_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_no_network,
                actionText = stringResource(R.string.auth_retry),
                onActionClick = { onIntent(LoginContract.Intent.LoginClicked) }
            )
        }
        uiState.serverError -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_error_server_title),
                subtitle = stringResource(R.string.auth_error_server_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_error,
                actionText = stringResource(R.string.auth_retry),
                onActionClick = { onIntent(LoginContract.Intent.LoginClicked) }
            )
        }
        else -> {
            LoginForm(uiState, onIntent)
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
private fun LoginForm(
    uiState: LoginContract.State,
    onIntent: (LoginContract.Intent) -> Unit
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(64.dp))

                Text(
                    text = stringResource(R.string.auth_login_welcome),
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.auth_login_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(48.dp))

                TripPointTextField(
                    value = uiState.email,
                    onValueChange = { onIntent(LoginContract.Intent.EmailChanged(it)) },
                    label = stringResource(R.string.auth_login_email_label),
                    placeholder = stringResource(R.string.auth_login_email_placeholder),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null)
                    },
                    isError = uiState.emailError != null,
                    errorMessage = uiState.emailError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(24.dp))

                TripPointTextField(
                    value = uiState.password,
                    onValueChange = { onIntent(LoginContract.Intent.PasswordChanged(it)) },
                    label = stringResource(R.string.auth_login_password_label),
                    placeholder = stringResource(R.string.auth_login_password_placeholder),
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
                            onIntent(LoginContract.Intent.TogglePasswordVisibility)
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
                    errorMessage = uiState.passwordError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = { onIntent(LoginContract.Intent.ForgotPasswordClicked) }) {
                        Text(
                            text = stringResource(R.string.auth_login_forgot_password),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TripPointButton(
                    text = stringResource(R.string.auth_login_button),
                    onClick = { onIntent(LoginContract.Intent.LoginClicked) },
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.auth_login_or_divider),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TripPointButton(
                        text = stringResource(R.string.auth_login_google),
                        onClick = { onIntent(LoginContract.Intent.GoogleLoginClicked) },
                        variant = ButtonVariant.Secondary,
                        modifier = Modifier.weight(1f)
                    )
                    TripPointButton(
                        text = stringResource(R.string.auth_login_apple),
                        onClick = { onIntent(LoginContract.Intent.AppleLoginClicked) },
                        variant = ButtonVariant.Secondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.auth_login_no_account),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = { onIntent(LoginContract.Intent.SignUpClicked) }) {
                    Text(
                        text = stringResource(R.string.auth_login_signup_link),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
