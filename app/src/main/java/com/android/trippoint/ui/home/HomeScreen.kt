package com.android.trippoint.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.data.repository.AuthRepositoryImpl
import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.authentication.domain.model.UserPreferences
import com.android.trippoint.authentication.domain.usecase.LogoutUseCase
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.designsystem.components.TripPointButton
import com.android.trippoint.core.network.AuthRemoteDataSource
import com.android.trippoint.core.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val api = NetworkModule.provideTripPointApi(
        authTokenProvider = { preferencesManager.getAuthToken() },
        refreshTokenProvider = { preferencesManager.getRefreshToken() },
        onTokenRefreshed = { token, refresh ->
            preferencesManager.setAuthToken(token)
            preferencesManager.setRefreshToken(refresh)
        }
    )
    val authRemoteDataSource = AuthRemoteDataSource(api)
    val authRepository = AuthRepositoryImpl(authRemoteDataSource, preferencesManager)
    val logoutUseCase = LogoutUseCase(authRepository)

    val viewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(logoutUseCase, authRepository) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    HomeScreen(
        uiState = uiState,
        onLogout = {
            viewModel.logout {
                onNavigateToLogin()
            }
        },
        onNavigateToProfile = onNavigateToProfile
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                androidx.compose.material3.IconButton(onClick = onNavigateToProfile) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.user != null -> {
                    Text(
                        text = "Hello, ${uiState.user.fullName ?: uiState.user.firstName ?: "User"}!",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = uiState.user.email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (uiState.user.username != null) {
                        Text(
                            text = "@${uiState.user.username}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    if (uiState.user.phoneNumber != null) {
                        Text(
                            text = uiState.user.phoneNumber!!,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProfileInfoItem("Country", uiState.user.country)
                        ProfileInfoItem("Nationality", uiState.user.nationality)
                        ProfileInfoItem("Date of Birth", uiState.user.dateOfBirth)
                        
                        if (uiState.preferences != null) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            ProfileInfoItem("Currency", uiState.preferences.currency)
                            ProfileInfoItem("Language", uiState.preferences.language)
                            ProfileInfoItem("Time Zone", uiState.preferences.timezone)
                        }
                    }
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            TripPointButton(
                text = "Logout",
                onClick = onLogout
            )
        }
    }
}

@Composable
private fun ProfileInfoItem(label: String, value: String?) {
    if (value != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val preferences: UserPreferences? = null,
    val error: String? = null
)

class HomeViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val repository: com.android.trippoint.authentication.domain.repository.AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val userResult = repository.getMe()
            val preferencesResult = repository.getMyPreferences()
            
            if (userResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = userResult.getOrNull(),
                    preferences = preferencesResult.getOrNull(),
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = userResult.exceptionOrNull()?.message ?: "Failed to load data"
                )
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = logoutUseCase()
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
}
