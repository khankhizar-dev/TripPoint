package com.android.trippoint.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.data.repository.AuthRepositoryImpl
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.designsystem.components.SettingsListItem
import com.android.trippoint.core.network.AuthRemoteDataSource
import com.android.trippoint.core.network.NetworkModule

@Composable
fun SecurityRoute(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val (viewModel, uiState) = rememberSecurityViewModel(context)

    SecurityScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun rememberSecurityViewModel(
    context: android.content.Context
): Pair<SecurityViewModel, SecurityContract.State> {
    val preferencesManager = androidx.compose.runtime.remember { PreferencesManager(context) }
    val authRemoteDataSource = androidx.compose.runtime.remember {
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
    val authRepository = androidx.compose.runtime.remember {
        AuthRepositoryImpl(authRemoteDataSource, preferencesManager)
    }

    val viewModel: SecurityViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SecurityViewModel(authRepository) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    
    return Pair(viewModel, uiState)
}

@Composable
fun SecurityScreen(
    uiState: SecurityContract.State,
    onIntent: (SecurityContract.Intent) -> Unit,
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
                    text = "Account & Security",
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
            ) {
                SettingsListItem(
                    title = "Change Password",
                    icon = Icons.Outlined.Lock,
                    onClick = { /* Navigate to Change Password */ }
                )
                
                SettingsListItem(
                    title = "Two-Factor Authentication",
                    icon = Icons.Outlined.VerifiedUser,
                    subtitle = if (uiState.isTwoFactorEnabled) "On" else "Off",
                    trailingContent = {
                        Switch(
                            checked = uiState.isTwoFactorEnabled,
                            onCheckedChange = { onIntent(SecurityContract.Intent.TwoFactorToggled(it)) }
                        )
                    }
                )
                
                SettingsListItem(
                    title = "Login Devices",
                    icon = Icons.Outlined.Devices,
                    subtitle = "${uiState.deviceCount} devices",
                    onClick = { /* Navigate to Device List */ }
                )
                
                SettingsListItem(
                    title = "Connected Accounts",
                    icon = Icons.Outlined.People,
                    onClick = { /* Navigate to Connected Accounts */ }
                )
                
                SettingsListItem(
                    title = "Delete Account",
                    icon = Icons.Outlined.Delete,
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = { onIntent(SecurityContract.Intent.DeleteAccountClicked) }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Security Checkup Card (Mock E)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF16A34A), // Success Green from Mock
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Security Checkup",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Your account is secure",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Last checkup: ${uiState.lastCheckupDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
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
