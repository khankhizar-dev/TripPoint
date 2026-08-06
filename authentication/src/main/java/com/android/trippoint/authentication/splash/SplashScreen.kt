package com.android.trippoint.authentication.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.R
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.designsystem.components.AppLogo
import com.android.trippoint.core.designsystem.components.FullscreenStatusView
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.SplashIllustration
import com.android.trippoint.core.designsystem.theme.TripPointTheme

@Composable
fun SplashRoute(
    onNavigateToWelcome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: SplashViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SplashViewModel(PreferencesManager(context)) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SplashContract.Effect.NavigateToWelcome -> onNavigateToWelcome()
                is SplashContract.Effect.NavigateToLogin -> onNavigateToLogin()
                is SplashContract.Effect.NavigateToProfileSetup -> onNavigateToProfileSetup()
                is SplashContract.Effect.NavigateToPermissions -> onNavigateToPermissions()
                is SplashContract.Effect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    SplashScreen(uiState = uiState)
}

@Composable
fun SplashScreen(uiState: SplashContract.State) {
    if (uiState.error != null) {
        val illustration = when (uiState.error) {
            SplashContract.SplashError.NoInternet -> 
                com.android.trippoint.core.designsystem.R.drawable.illustration_no_network
            SplashContract.SplashError.ServerError -> 
                com.android.trippoint.core.designsystem.R.drawable.illustration_error
            SplashContract.SplashError.Maintenance -> 
                com.android.trippoint.core.designsystem.R.drawable.illustration_plan
            SplashContract.SplashError.ForceUpdate -> 
                com.android.trippoint.core.designsystem.R.drawable.illustration_trip
        }

        FullscreenStatusView(
            title = stringResource(uiState.error.titleResId),
            subtitle = stringResource(uiState.error.descriptionResId),
            imageResId = illustration,
            actionText = stringResource(com.android.trippoint.core.designsystem.R.string.core_designsystem_retry),
            onActionClick = { /* Handle retry */ }
        )
        return
    }

    val dimen = TripPointTheme.dimensions
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            AppLogo()

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(R.string.auth_app_name),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.auth_splash_tagline),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            SplashIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimen.illustrationHeight)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                LoadingIndicator()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(uiState.splashStep.messageResId),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "v1.0.0",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
