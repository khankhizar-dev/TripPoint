package com.android.trippoint.authentication.splash

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SystemUpdate
import com.android.trippoint.authentication.R
import org.junit.Assert.assertEquals
import org.junit.Test

class SplashContractTest {

    @Test
    fun `verify splash steps message resource ids`() {
        assertEquals(
            R.string.auth_splash_step_initializing,
            SplashContract.SplashStep.Initializing.messageResId
        )
        assertEquals(
            R.string.auth_splash_step_checking_version,
            SplashContract.SplashStep.CheckingVersion.messageResId
        )
        assertEquals(
            R.string.auth_splash_step_syncing,
            SplashContract.SplashStep.SyncingData.messageResId
        )
    }

    @Test
    fun `verify splash error properties`() {
        val errors = listOf(
            SplashContract.SplashError.NoInternet to Triple(
                R.string.auth_splash_error_no_internet_title,
                R.string.auth_splash_error_no_internet_desc,
                Icons.Default.CloudOff
            ),
            SplashContract.SplashError.ServerError to Triple(
                R.string.auth_splash_error_server_title,
                R.string.auth_splash_error_server_desc,
                Icons.Default.Error
            ),
            SplashContract.SplashError.Maintenance to Triple(
                R.string.auth_splash_error_maintenance_title,
                R.string.auth_splash_error_maintenance_desc,
                Icons.Default.Build
            ),
            SplashContract.SplashError.ForceUpdate to Triple(
                R.string.auth_splash_error_force_update_title,
                R.string.auth_splash_error_force_update_desc,
                Icons.Default.SystemUpdate
            )
        )

        errors.forEach { (error, expected) ->
            assertEquals(expected.first, error.titleResId)
            assertEquals(expected.second, error.descriptionResId)
            assertEquals(expected.third, error.icon)
        }
    }

    @Test
    fun `verify initial state`() {
        val state = SplashContract.State()
        assertEquals(SplashContract.SplashStep.Initializing, state.splashStep)
        assertEquals(null, state.error)
    }
}
