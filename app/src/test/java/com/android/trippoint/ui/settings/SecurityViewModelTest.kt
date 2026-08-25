package com.android.trippoint.ui.settings

import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.core.network.UserDevice
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SecurityViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: SecurityViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { authRepository.getUserDevices() } returns Result.success(emptyList())
        viewModel = SecurityViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSecurityData success updates device count`() = runTest {
        val devices = listOf(mockk<UserDevice>(), mockk<UserDevice>())
        coEvery { authRepository.getUserDevices() } returns Result.success(devices)

        viewModel.onIntent(SecurityContract.Intent.Refresh)
        runCurrent()

        assertEquals(2, viewModel.uiState.value.deviceCount)
    }

    @Test
    fun `two factor toggle updates state`() {
        viewModel.onIntent(SecurityContract.Intent.TwoFactorToggled(true))
        assertEquals(true, viewModel.uiState.value.isTwoFactorEnabled)
    }
}
