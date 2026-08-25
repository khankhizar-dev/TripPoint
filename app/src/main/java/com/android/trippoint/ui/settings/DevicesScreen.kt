package com.android.trippoint.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.data.repository.AuthRepositoryImpl
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.network.AuthRemoteDataSource
import com.android.trippoint.core.network.NetworkModule
import com.android.trippoint.core.network.UserDevice

@Composable
fun DevicesRoute(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val (viewModel, uiState) = rememberDevicesViewModel(context)

    DevicesScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun rememberDevicesViewModel(
    context: android.content.Context
): Pair<DevicesViewModel, DevicesContract.State> {
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

    val viewModel: DevicesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DevicesViewModel(authRepository) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    
    return Pair(viewModel, uiState)
}

@Composable
fun DevicesScreen(
    uiState: DevicesContract.State,
    onIntent: (DevicesContract.Intent) -> Unit,
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
                    text = "Login Devices",
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
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.devices) { device ->
                        DeviceItem(device)
                    }
                    
                    if (uiState.devices.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            TripPointButton(
                                text = "Logout from All Devices",
                                onClick = { onIntent(DevicesContract.Intent.LogoutAllDevices) },
                                variant = com.android.trippoint.core.designsystem.components.ButtonVariant.Secondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceItem(device: UserDevice) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = if (device.platform?.lowercase()?.contains("android") == true) {
                Icons.Outlined.PhoneAndroid
            } else {
                Icons.Outlined.DesktopWindows
            }
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.deviceName ?: "Unknown Device",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${device.platform ?: "Unknown Platform"} • App v${device.appVersion ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Last active: ${device.lastLoginAt ?: "Never"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun TripPointButton(
    text: String,
    onClick: () -> Unit,
    variant: com.android.trippoint.core.designsystem.components.ButtonVariant
) {
    com.android.trippoint.core.designsystem.components.TripPointButton(
        text = text,
        onClick = onClick,
        variant = variant
    )
}
