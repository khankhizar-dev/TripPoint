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
class DevicesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: DevicesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { authRepository.getUserDevices() } returns Result.success(emptyList())
        viewModel = DevicesViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDevices success updates state`() = runTest {
        val devices = listOf(
            UserDevice("1", "Device 1", "Android", "1.0", "now", "then"),
            UserDevice("2", "Device 2", "Web", "1.0", "now", "then")
        )
        coEvery { authRepository.getUserDevices() } returns Result.success(devices)

        viewModel.onIntent(DevicesContract.Intent.LoadDevices)
        runCurrent()

        assertEquals(2, viewModel.uiState.value.devices.size)
        assertEquals("Device 1", viewModel.uiState.value.devices[0].deviceName)
    }
}
