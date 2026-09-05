package com.android.trippoint.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.android.trippoint.core.network.AuthRemoteDataSource
import com.android.trippoint.core.network.NetworkModule
import com.android.trippoint.core.network.TripRemoteDataSource
import com.android.trippoint.trip.data.repository.TripRepositoryImpl
import com.android.trippoint.trip.list.TripListRoute
import com.android.trippoint.trip.list.TripListViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToTripDetails: (String) -> Unit,
    onNavigateToCreateTrip: (String) -> Unit,
    onNavigateToBookings: (String?) -> Unit,
    onNavigateToBudgets: (String?) -> Unit
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

    val tripRemoteDataSource = TripRemoteDataSource(api)
    val tripRepository = TripRepositoryImpl(tripRemoteDataSource)

    val viewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(logoutUseCase, authRepository) as T
            }
        }
    )

    val tripListViewModel: TripListViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TripListViewModel(tripRepository) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    HomeScreen(
        uiState = uiState,
        tripListViewModel = tripListViewModel,
        onLogout = {
            viewModel.logout {
                onNavigateToLogin()
            }
        },
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToTripDetails = onNavigateToTripDetails,
        onNavigateToCreateTrip = { onNavigateToCreateTrip("") }, // Pass empty or dynamic tripId if needed
        onNavigateToBookings = onNavigateToBookings
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    tripListViewModel: TripListViewModel,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToTripDetails: (String) -> Unit,
    onNavigateToCreateTrip: () -> Unit,
    onNavigateToBookings: (String?) -> Unit
) {
    // We delegate the main UI to TripListRoute which has its own Scaffold
    // The user greeting can be added as a custom header if we modify TripListScreen,
    // but for now let's just show the Trip Workspace as the primary part of Home.
    
    TripListRoute(
        viewModel = tripListViewModel,
        onNavigateToDetails = onNavigateToTripDetails,
        onNavigateToCreate = { onNavigateToCreateTrip() },
        onNavigateToBookings = onNavigateToBookings,
        onNavigateToProfile = onNavigateToProfile,
        userName = uiState.user?.fullName ?: uiState.user?.firstName
    )
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
