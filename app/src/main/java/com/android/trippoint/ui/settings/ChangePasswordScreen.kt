package com.android.trippoint.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.data.repository.AuthRepositoryImpl
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.designsystem.components.TripPointTextField
import com.android.trippoint.core.network.AuthRemoteDataSource
import com.android.trippoint.core.network.NetworkModule

@Composable
fun ChangePasswordRoute(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val (viewModel, uiState) = rememberChangePasswordViewModel(context)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ChangePasswordContract.Effect.NavigateBack -> onNavigateBack()
                ChangePasswordContract.Effect.ShowSuccess -> { /* Show success toast */ }
                is ChangePasswordContract.Effect.ShowError -> { /* Show error toast */ }
            }
        }
    }

    ChangePasswordScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun rememberChangePasswordViewModel(
    context: android.content.Context
): Pair<ChangePasswordViewModel, ChangePasswordContract.State> {
    val preferencesManager = remember { PreferencesManager(context) }
    val authRemoteDataSource = remember {
        val api = NetworkModule.provideTripPointApi(
            authTokenProvider = { preferencesManager.getAuthToken() },
            refreshTokenProvider = { preferencesManager.getRefreshToken() },
            onTokenRefreshed = { token, refresh ->
                preferencesManager.setAuthToken(token)
                preferencesManager.setRefreshToken(refresh)
            }
        )
        AuthRemoteDataSource(api)
    }
    val authRepository = remember {
        AuthRepositoryImpl(authRemoteDataSource, preferencesManager)
    }

    val viewModel: ChangePasswordViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChangePasswordViewModel(authRepository) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    
    return Pair(viewModel, uiState)
}

@Composable
fun ChangePasswordScreen(
    uiState: ChangePasswordContract.State,
    onIntent: (ChangePasswordContract.Intent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Change Password",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                PasswordField(
                    value = uiState.currentPassword,
                    onValueChange = { onIntent(ChangePasswordContract.Intent.CurrentPasswordChanged(it)) },
                    label = "Current Password",
                    isVisible = uiState.isCurrentVisible,
                    onToggleVisibility = { onIntent(ChangePasswordContract.Intent.ToggleCurrentVisibility) },
                    error = uiState.currentError
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                PasswordField(
                    value = uiState.newPassword,
                    onValueChange = { onIntent(ChangePasswordContract.Intent.NewPasswordChanged(it)) },
                    label = "New Password",
                    isVisible = uiState.isNewVisible,
                    onToggleVisibility = { onIntent(ChangePasswordContract.Intent.ToggleNewVisibility) },
                    error = uiState.newError
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                PasswordField(
                    value = uiState.confirmPassword,
                    onValueChange = { onIntent(ChangePasswordContract.Intent.ConfirmPasswordChanged(it)) },
                    label = "Confirm New Password",
                    isVisible = uiState.isConfirmVisible,
                    onToggleVisibility = { onIntent(ChangePasswordContract.Intent.ToggleConfirmVisibility) },
                    error = uiState.confirmError
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                TripPointButton(
                    text = "Update Password",
                    onClick = { onIntent(ChangePasswordContract.Intent.SaveClicked) },
                    enabled = !uiState.isLoading
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String?
) {
    TripPointTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        isError = error != null,
        errorMessage = error
    )
}
