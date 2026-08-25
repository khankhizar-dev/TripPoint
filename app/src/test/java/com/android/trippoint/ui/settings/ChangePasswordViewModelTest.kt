package com.android.trippoint.ui.settings

import app.cash.turbine.test
import com.android.trippoint.authentication.domain.repository.AuthRepository
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
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChangePasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: ChangePasswordViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChangePasswordViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `savePassword with empty current password shows error`() {
        viewModel.onIntent(ChangePasswordContract.Intent.SaveClicked)
        assertNotNull(viewModel.uiState.value.currentError)
    }

    @Test
    fun `savePassword with short new password shows error`() {
        viewModel.onIntent(ChangePasswordContract.Intent.CurrentPasswordChanged("current"))
        viewModel.onIntent(ChangePasswordContract.Intent.NewPasswordChanged("short"))
        viewModel.onIntent(ChangePasswordContract.Intent.SaveClicked)
        assertNotNull(viewModel.uiState.value.newError)
    }

    @Test
    fun `savePassword with mismatch confirm password shows error`() {
        viewModel.onIntent(ChangePasswordContract.Intent.CurrentPasswordChanged("current"))
        viewModel.onIntent(ChangePasswordContract.Intent.NewPasswordChanged("newpassword123"))
        viewModel.onIntent(ChangePasswordContract.Intent.ConfirmPasswordChanged("different"))
        viewModel.onIntent(ChangePasswordContract.Intent.SaveClicked)
        assertNotNull(viewModel.uiState.value.confirmError)
    }

    @Test
    fun `savePassword success navigates back`() = runTest {
        coEvery { authRepository.changePassword(any(), any()) } returns Result.success(true)

        viewModel.onIntent(ChangePasswordContract.Intent.CurrentPasswordChanged("current"))
        viewModel.onIntent(ChangePasswordContract.Intent.NewPasswordChanged("newpassword123"))
        viewModel.onIntent(ChangePasswordContract.Intent.ConfirmPasswordChanged("newpassword123"))

        viewModel.effect.test {
            viewModel.onIntent(ChangePasswordContract.Intent.SaveClicked)
            runCurrent()

            assertEquals(ChangePasswordContract.Effect.ShowSuccess, awaitItem())
            assertEquals(ChangePasswordContract.Effect.NavigateBack, awaitItem())
        }
    }
}
