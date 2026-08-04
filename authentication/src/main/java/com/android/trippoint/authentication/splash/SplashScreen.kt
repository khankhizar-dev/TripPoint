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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.R
import com.android.trippoint.core.designsystem.components.AppLogo
import com.android.trippoint.core.designsystem.components.ErrorView
import com.android.trippoint.core.designsystem.components.LoadingIndicator
import com.android.trippoint.core.designsystem.components.SplashIllustration
import com.android.trippoint.core.designsystem.theme.TripPointTheme

@Composable
fun SplashRoute(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SplashContract.Effect.NavigateToLogin -> onNavigateToLogin()
                is SplashContract.Effect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    SplashScreen(uiState = uiState)
}

@Composable
fun SplashScreen(uiState: SplashContract.State) {
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
                if (uiState.error != null) {
                    val retryText = stringResource(
                        com.android.trippoint.core.designsystem.R.string.core_designsystem_retry
                    )
                    ErrorView(
                        title = stringResource(uiState.error.titleResId),
                        description = stringResource(uiState.error.descriptionResId),
                        icon = uiState.error.icon,
                        actionText = retryText,
                        onActionClick = { /* Handle retry */ }
                    )
                } else {
                    LoadingIndicator()

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(uiState.splashStep.messageResId),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
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
