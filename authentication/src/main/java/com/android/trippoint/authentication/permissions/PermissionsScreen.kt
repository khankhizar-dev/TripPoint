package com.android.trippoint.authentication.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.trippoint.authentication.R
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.designsystem.components.ButtonVariant
import com.android.trippoint.core.designsystem.components.FullscreenStatusView
import com.android.trippoint.core.designsystem.components.SplashIllustration
import com.android.trippoint.core.designsystem.components.TripPointButton

@Composable
fun PermissionsRoute(
    onNavigateToHome: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: PermissionsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PermissionsViewModel(PreferencesManager(context)) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PermissionsContract.Effect.NavigateToHome -> onNavigateToHome()
                is PermissionsContract.Effect.RequestPermission -> {
                    // Trigger system permission dialog here in a real app
                }
            }
        }
    }

    PermissionsScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun PermissionsScreen(
    uiState: PermissionsContract.State,
    onIntent: (PermissionsContract.Intent) -> Unit
) {
    if (uiState.isAllSet) {
        FullscreenStatusView(
            title = stringResource(R.string.auth_permission_all_set_title),
            subtitle = stringResource(R.string.auth_permission_all_set_subtitle),
            imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_success,
            actionText = stringResource(R.string.auth_permission_explore),
            onActionClick = { onIntent(PermissionsContract.Intent.ExploreClicked) }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            val scene = when (uiState.currentStep) {
                PermissionsContract.Step.NOTIFICATIONS -> PermissionScene(
                    titleResId = R.string.auth_permission_notifications_title,
                    subtitleResId = R.string.auth_permission_notifications_subtitle,
                    imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_plan // Placeholder
                )
                PermissionsContract.Step.LOCATION -> PermissionScene(
                    titleResId = R.string.auth_permission_location_title,
                    subtitleResId = R.string.auth_permission_location_subtitle,
                    imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_offline // Placeholder
                )
                PermissionsContract.Step.CALENDAR -> PermissionScene(
                    titleResId = R.string.auth_permission_calendar_title,
                    subtitleResId = R.string.auth_permission_calendar_subtitle,
                    imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_trip // Placeholder
                )
            }

            SplashIllustration(
                imageResId = scene.imageResId,
                modifier = Modifier.height(240.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(scene.titleResId),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(scene.subtitleResId),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.weight(1f))

            TripPointButton(
                text = stringResource(R.string.auth_permission_allow),
                onClick = { onIntent(PermissionsContract.Intent.AllowClicked) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TripPointButton(
                text = stringResource(R.string.auth_permission_deny),
                onClick = { onIntent(PermissionsContract.Intent.DenyClicked) },
                variant = ButtonVariant.Text
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private data class PermissionScene(
    val titleResId: Int,
    val subtitleResId: Int,
    val imageResId: Int
)
