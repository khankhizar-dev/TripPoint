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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.designsystem.components.CompletionCard
import com.android.trippoint.core.designsystem.components.ProfileHeader
import com.android.trippoint.core.designsystem.components.SettingsListItem

@Composable
fun SettingsRoute(
    onNavigateToLogin: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val context = LocalContext.current
    val (viewModel, uiState) = rememberSettingsViewModel(context)

    LaunchedEffect(Unit) {
        viewModel.onIntent(SettingsContract.Intent.LoadUser)
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsContract.Effect.NavigateToLogin -> onNavigateToLogin()
                is SettingsContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    SettingsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateToEditProfile = onNavigateToEditProfile,
        onNavigateToPreferences = onNavigateToPreferences,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToSecurity = onNavigateToSecurity,
        onNavigateToSupport = onNavigateToSupport,
        onNavigateToAbout = onNavigateToAbout
    )
}

private fun calculateCompletion(user: com.android.trippoint.authentication.domain.model.User): Int {
    val fields = listOf(
        user.firstName,
        user.lastName,
        user.username,
        user.profilePhotoUrl,
        user.phoneNumber,
        user.dateOfBirth,
        user.nationality,
        user.country
    )
    val filledFields = fields.count { !it.isNullOrBlank() }
    return (filledFields.toFloat() / fields.size * 100).toInt()
}

@Composable
private fun rememberSettingsViewModel(
    context: android.content.Context
): Pair<SettingsViewModel, SettingsContract.State> {
    val preferencesManager = androidx.compose.runtime.remember { PreferencesManager(context) }
    val authRemoteDataSource = androidx.compose.runtime.remember {
        val api = com.android.trippoint.core.network.NetworkModule.provideTripPointApi(
            authTokenProvider = { preferencesManager.getAuthToken() },
            refreshTokenProvider = { preferencesManager.getRefreshToken() },
            onTokenRefreshed = { token, refresh ->
                preferencesManager.setAuthToken(token)
                preferencesManager.setRefreshToken(refresh)
            }
        )
        com.android.trippoint.core.network.AuthRemoteDataSource(api)
    }
    val authRepository = androidx.compose.runtime.remember {
        com.android.trippoint.authentication.data.repository.AuthRepositoryImpl(
            authRemoteDataSource, preferencesManager
        )
    }

    val viewModel: SettingsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(authRepository) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    
    return Pair(viewModel, uiState)
}

@Composable
fun SettingsScreen(
    uiState: SettingsContract.State,
    onIntent: (SettingsContract.Intent) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    Scaffold(
        topBar = {
            SettingsTopBar()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            SettingsContent(
                uiState = uiState,
                onIntent = onIntent,
                onNavigateToEditProfile = onNavigateToEditProfile,
                onNavigateToPreferences = onNavigateToPreferences,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToSecurity = onNavigateToSecurity,
                onNavigateToSupport = onNavigateToSupport,
                onNavigateToAbout = onNavigateToAbout
            )

            if (uiState.isLoading) {
                SettingsLoadingOverlay()
            }
        }
    }
}

@Composable
private fun SettingsTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsContract.State,
    onIntent: (SettingsContract.Intent) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val user = uiState.user
        if (user != null) {
            ProfileHeader(
                name = "${user.firstName ?: ""} ${user.lastName ?: ""}"
                    .trim().ifEmpty { "User" },
                email = user.email,
                imageUrl = user.profilePhotoUrl
            )
            
            Button(
                onClick = onNavigateToEditProfile,
                modifier = Modifier.width(160.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = "Edit Profile")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val completionPercentage = calculateCompletion(user)
            CompletionCard(completionPercentage = completionPercentage)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SettingsOptionsList(
                onNavigateToPreferences = onNavigateToPreferences,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToSecurity = onNavigateToSecurity,
                onNavigateToSupport = onNavigateToSupport,
                onNavigateToAbout = onNavigateToAbout
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            LogoutButton(onLogout = { onIntent(SettingsContract.Intent.Logout) })
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsOptionsList(
    onNavigateToPreferences: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsListItem(
            title = "Preferences",
            icon = Icons.Outlined.Settings,
            onClick = onNavigateToPreferences
        )
        SettingsListItem(
            title = "Notifications",
            icon = Icons.Outlined.Notifications,
            onClick = onNavigateToNotifications
        )
        SettingsListItem(
            title = "Account & Security",
            icon = Icons.Outlined.Lock,
            onClick = onNavigateToSecurity
        )
        SettingsListItem(
            title = "Support & Help",
            icon = Icons.Outlined.QuestionMark,
            onClick = onNavigateToSupport
        )
        SettingsListItem(
            title = "About TripPoint",
            icon = Icons.Default.Info,
            onClick = onNavigateToAbout
        )
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    TextButton(onClick = onLogout) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Log Out",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SettingsLoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}