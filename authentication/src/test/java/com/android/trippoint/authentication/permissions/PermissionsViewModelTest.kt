package com.android.trippoint.authentication.permissions

import app.cash.turbine.test
import com.android.trippoint.core.database.preferences.PreferencesManager
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PermissionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val preferencesManager: PreferencesManager = mockk(relaxed = true)
    private lateinit var viewModel: PermissionsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PermissionsViewModel(preferencesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial step is NOTIFICATIONS`() {
        assertEquals(PermissionsContract.Step.NOTIFICATIONS, viewModel.uiState.value.currentStep)
        assertEquals(false, viewModel.uiState.value.isAllSet)
    }

    @Test
    fun `allow clicked updates steps and sends effect`() = runTest {
        viewModel.effect.test {
            // NOTIFICATIONS -> LOCATION
            viewModel.onIntent(PermissionsContract.Intent.AllowClicked)
            assertEquals(
                PermissionsContract.Effect.RequestPermission(PermissionsContract.Step.NOTIFICATIONS),
                awaitItem()
            )
            assertEquals(PermissionsContract.Step.LOCATION, viewModel.uiState.value.currentStep)

            // LOCATION -> CALENDAR
            viewModel.onIntent(PermissionsContract.Intent.AllowClicked)
            assertEquals(
                PermissionsContract.Effect.RequestPermission(PermissionsContract.Step.LOCATION),
                awaitItem()
            )
            assertEquals(PermissionsContract.Step.CALENDAR, viewModel.uiState.value.currentStep)

            // CALENDAR -> ALL SET
            viewModel.onIntent(PermissionsContract.Intent.AllowClicked)
            assertEquals(
                PermissionsContract.Effect.RequestPermission(PermissionsContract.Step.CALENDAR),
                awaitItem()
            )
            assertEquals(true, viewModel.uiState.value.isAllSet)
        }
    }

    @Test
    fun `deny clicked updates steps without effect`() = runTest {
        // NOTIFICATIONS -> LOCATION
        viewModel.onIntent(PermissionsContract.Intent.DenyClicked)
        assertEquals(PermissionsContract.Step.LOCATION, viewModel.uiState.value.currentStep)

        // LOCATION -> CALENDAR
        viewModel.onIntent(PermissionsContract.Intent.DenyClicked)
        assertEquals(PermissionsContract.Step.CALENDAR, viewModel.uiState.value.currentStep)

        // CALENDAR -> ALL SET
        viewModel.onIntent(PermissionsContract.Intent.DenyClicked)
        assertEquals(true, viewModel.uiState.value.isAllSet)
    }

    @Test
    fun `explore clicked saves status and navigates home`() = runTest {
        viewModel.onIntent(PermissionsContract.Intent.DenyClicked) // to LOCATION
        viewModel.onIntent(PermissionsContract.Intent.DenyClicked) // to CALENDAR
        viewModel.onIntent(PermissionsContract.Intent.DenyClicked) // to ALL SET
        
        viewModel.effect.test {
            viewModel.onIntent(PermissionsContract.Intent.ExploreClicked)
            verify { preferencesManager.setPermissionsRequested(true) }
            assertEquals(PermissionsContract.Effect.NavigateToHome, awaitItem())
        }
    }
}
