package com.android.trippoint.authentication.permissions

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val authRepository = com.android.trippoint.authentication.data.repository.AuthRepositoryImpl(
        com.android.trippoint.core.network.AuthRemoteDataSource(
            com.android.trippoint.core.network.NetworkModule.provideTripPointApi(
                { preferencesManager.getAuthToken() }, 
                { preferencesManager.getRefreshToken() }, 
                { t, r -> 
                    preferencesManager.setAuthToken(t)
                    preferencesManager.setRefreshToken(r)
                }
            )
        ),
        preferencesManager
    )
    val viewModel: PermissionsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PermissionsViewModel(authRepository) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> 
        viewModel.onIntent(PermissionsContract.Intent.PermissionHandled)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PermissionsContract.Effect.NavigateToHome -> onNavigateToHome()
                is PermissionsContract.Effect.RequestPermission -> {
                    val permission = when (effect.permission) {
                        PermissionsContract.Step.NOTIFICATIONS -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.POST_NOTIFICATIONS
                            } else null
                        }
                        PermissionsContract.Step.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
                        PermissionsContract.Step.CALENDAR -> Manifest.permission.READ_CALENDAR
                    }
                    if (permission != null) {
                        permissionLauncher.launch(permission)
                    } else {
                        viewModel.onIntent(PermissionsContract.Intent.PermissionHandled)
                    }
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
        Surface(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            FullscreenStatusView(
                title = stringResource(R.string.auth_permission_all_set_title),
                subtitle = stringResource(R.string.auth_permission_all_set_subtitle),
                imageResId = com.android.trippoint.core.designsystem.R.drawable.illustration_success,
                actionText = stringResource(R.string.auth_permission_explore),
                onActionClick = { onIntent(PermissionsContract.Intent.ExploreClicked) },
                includeBackground = false,
                modifier = Modifier.wrapContentHeight()
            )
        }
        return
    }

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .widthIn(max = 400.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                modifier = Modifier.height(180.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(scene.titleResId),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(scene.subtitleResId),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            TripPointButton(
                text = stringResource(R.string.auth_permission_allow),
                onClick = { onIntent(PermissionsContract.Intent.AllowClicked) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TripPointButton(
                text = stringResource(R.string.auth_permission_deny),
                onClick = { onIntent(PermissionsContract.Intent.DenyClicked) },
                variant = ButtonVariant.Text
            )
        }
    }
}

private data class PermissionScene(
    val titleResId: Int,
    val subtitleResId: Int,
    val imageResId: Int
)
