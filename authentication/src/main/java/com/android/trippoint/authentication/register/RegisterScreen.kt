package com.android.trippoint.authentication.register

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.R
import com.android.trippoint.core.designsystem.components.FullscreenStatusView
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTextField

@Composable
fun RegisterRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterContract.Effect.NavigateToHome -> onNavigateToHome()
                is RegisterContract.Effect.NavigateToLogin -> onNavigateToLogin()
                is RegisterContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    RegisterScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun RegisterScreen(
    uiState: RegisterContract.State,
    onIntent: (RegisterContract.Intent) -> Unit
) {
    when {
        uiState.isSuccess -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_register_success_title),
                subtitle = stringResource(R.string.auth_register_success_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_success
            )
        }
        uiState.offlineError -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_error_offline_title),
                subtitle = stringResource(R.string.auth_error_offline_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_no_network,
                actionText = stringResource(R.string.auth_retry),
                onActionClick = { onIntent(RegisterContract.Intent.RegisterClicked) }
            )
        }
        uiState.serverError -> {
            FullscreenStatusView(
                title = stringResource(R.string.auth_error_server_title),
                subtitle = stringResource(R.string.auth_error_server_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_error,
                actionText = stringResource(R.string.auth_retry),
                onActionClick = { onIntent(RegisterContract.Intent.RegisterClicked) }
            )
        }
        else -> {
            RegisterForm(uiState, onIntent)
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
private fun RegisterForm(
    uiState: RegisterContract.State,
    onIntent: (RegisterContract.Intent) -> Unit
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    text = stringResource(R.string.auth_register_title),
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.auth_register_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(48.dp))

                TripPointTextField(
                    value = uiState.name,
                    onValueChange = { onIntent(RegisterContract.Intent.NameChanged(it)) },
                    label = stringResource(R.string.auth_register_name_label),
                    placeholder = stringResource(R.string.auth_register_name_placeholder),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    },
                    isError = uiState.nameError != null,
                    errorMessage = uiState.nameError?.let { stringResource(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                TripPointTextField(
                    value = uiState.email,
                    onValueChange = { onIntent(RegisterContract.Intent.EmailChanged(it)) },
                    label = stringResource(R.string.auth_register_email_label),
                    placeholder = stringResource(R.string.auth_register_email_placeholder),
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
                    onValueChange = { onIntent(RegisterContract.Intent.PasswordChanged(it)) },
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
                            onIntent(RegisterContract.Intent.TogglePasswordVisibility)
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

                Spacer(modifier = Modifier.height(24.dp))

                TripPointTextField(
                    value = uiState.confirmPassword,
                    onValueChange = { onIntent(RegisterContract.Intent.ConfirmPasswordChanged(it)) },
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
                            onIntent(RegisterContract.Intent.ToggleConfirmPasswordVisibility)
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
                    errorMessage = uiState.confirmPasswordError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(32.dp))

                TripPointButton(
                    text = stringResource(R.string.auth_register_button),
                    onClick = { onIntent(RegisterContract.Intent.RegisterClicked) },
                    enabled = !uiState.isLoading
                )

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
                    text = stringResource(R.string.auth_register_already_have_account),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = { onIntent(RegisterContract.Intent.LoginClicked) }) {
                    Text(
                        text = stringResource(R.string.auth_register_login_link),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
