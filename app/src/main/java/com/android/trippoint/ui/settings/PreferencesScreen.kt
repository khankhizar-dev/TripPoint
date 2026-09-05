package com.android.trippoint.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import com.android.trippoint.authentication.data.repository.AuthRepositoryImpl
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.designsystem.components.SettingsListItem
import com.android.trippoint.core.network.AuthRemoteDataSource
import com.android.trippoint.core.network.NetworkModule

@Composable
fun PreferencesRoute(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val (viewModel, uiState) = rememberPreferencesViewModel(context)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PreferencesContract.Effect.NavigateBack -> onNavigateBack()
                is PreferencesContract.Effect.ShowError -> { /* Handle error */ }
            }
        }
    }

    PreferencesScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun rememberPreferencesViewModel(
    context: android.content.Context
): Pair<PreferencesViewModel, PreferencesContract.State> {
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

    val viewModel: PreferencesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PreferencesViewModel(authRepository) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    
    return Pair(viewModel, uiState)
}

@Composable
fun PreferencesScreen(
    uiState: PreferencesContract.State,
    onIntent: (PreferencesContract.Intent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            PreferencesTopBar(onNavigateBack)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            PreferencesContent(uiState, onIntent)

            if (uiState.isLoading) {
                PreferencesLoadingOverlay()
            }
        }
    }
}

@Composable
private fun PreferencesTopBar(onNavigateBack: () -> Unit) {
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
            text = "Preferences",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun PreferencesContent(
    uiState: PreferencesContract.State,
    onIntent: (PreferencesContract.Intent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SettingsListItem(
            title = "Currency",
            icon = Icons.Outlined.CurrencyExchange,
            subtitle = uiState.currency,
            onClick = { 
                val current = uiState.currency
                val next = if (current == "INR") {
                    "USD"
                } else if (current == "USD") {
                    "EUR"
                } else {
                    "INR"
                }
                onIntent(PreferencesContract.Intent.CurrencyChanged(next))
            }
        )
        SettingsListItem(
            title = "Language",
            icon = Icons.Outlined.Language,
            subtitle = if (uiState.language == "en") {
                "English"
            } else if (uiState.language == "hi") {
                "Hindi"
            } else {
                uiState.language
            },
            onClick = { 
                val next = if (uiState.language == "en") "hi" else "en"
                onIntent(PreferencesContract.Intent.LanguageChanged(next))
            }
        )
        SettingsListItem(
            title = "Date Format",
            icon = Icons.Outlined.CalendarMonth,
            subtitle = uiState.dateFormat,
            onClick = { 
                val next = if (uiState.dateFormat == "DD/MM/YYYY") "MM/DD/YYYY" else "DD/MM/YYYY"
                onIntent(PreferencesContract.Intent.DateFormatChanged(next))
            }
        )
        SettingsListItem(
            title = "Units",
            icon = Icons.Outlined.Straighten,
            subtitle = if (uiState.units == "METRIC") "Metric (km, °C)" else "Imperial (mi, °F)",
            onClick = { 
                val next = if (uiState.units == "METRIC") "IMPERIAL" else "METRIC"
                onIntent(PreferencesContract.Intent.UnitsChanged(next))
            }
        )
        SettingsListItem(
            title = "Theme",
            icon = Icons.Outlined.DarkMode,
            trailingContent = {
                Switch(
                    checked = uiState.isDarkTheme,
                    onCheckedChange = { onIntent(PreferencesContract.Intent.ThemeChanged(it)) }
                )
            },
            onClick = { onIntent(PreferencesContract.Intent.ThemeChanged(!uiState.isDarkTheme)) }
        )
    }
}

@Composable
private fun PreferencesLoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}