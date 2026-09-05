package com.android.trippoint.authentication.splash

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.trippoint.authentication.R
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class SplashContract {
    sealed class Intent : UiIntent {
        object CheckAuth : Intent()
    }

    data class State(
        val splashStep: SplashStep = SplashStep.Initializing,
        val error: SplashError? = null
    ) : UiState

    sealed class SplashStep(val messageResId: Int) {
        object Initializing : SplashStep(R.string.auth_splash_step_initializing)
        object CheckingVersion : SplashStep(R.string.auth_splash_step_checking_version)
        object SyncingData : SplashStep(R.string.auth_splash_step_syncing)
    }

    sealed class SplashError(
        val titleResId: Int,
        val descriptionResId: Int,
        val icon: ImageVector
    ) {
        object NoInternet : SplashError(
            titleResId = R.string.auth_splash_error_no_internet_title,
            descriptionResId = R.string.auth_splash_error_no_internet_desc,
            icon = Icons.Default.CloudOff
        )
        object ServerError : SplashError(
            titleResId = R.string.auth_splash_error_server_title,
            descriptionResId = R.string.auth_splash_error_server_desc,
            icon = Icons.Default.Error
        )
        object Maintenance : SplashError(
            titleResId = R.string.auth_splash_error_maintenance_title,
            descriptionResId = R.string.auth_splash_error_maintenance_desc,
            icon = Icons.Default.Build
        )
        object ForceUpdate : SplashError(
            titleResId = R.string.auth_splash_error_force_update_title,
            descriptionResId = R.string.auth_splash_error_force_update_desc,
            icon = Icons.Default.SystemUpdate
        )
    }

    sealed class Effect : UiEffect {
        object NavigateToWelcome : Effect()
        object NavigateToLogin : Effect()
        object NavigateToProfileSetup : Effect()
        object NavigateToPermissions : Effect()
        object NavigateToHome : Effect()
    }
}
