package com.android.trippoint.authentication.register

import app.cash.turbine.test
import com.android.trippoint.authentication.R
import com.android.trippoint.core.database.preferences.PreferencesManager
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val preferencesManager: PreferencesManager = mockk(relaxed = true)
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegisterViewModel(preferencesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertEquals("", state.confirmPassword)
        assertNull(state.nameError)
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertNull(state.confirmPasswordError)
    }

    @Test
    fun `name change updates state`() {
        viewModel.onIntent(RegisterContract.Intent.NameChanged("John Doe"))
        assertEquals("John Doe", viewModel.uiState.value.name)
    }

    @Test
    fun `email change updates state`() {
        viewModel.onIntent(RegisterContract.Intent.EmailChanged("test@example.com"))
        assertEquals("test@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun `password change updates state`() {
        viewModel.onIntent(RegisterContract.Intent.PasswordChanged("password123"))
        assertEquals("password123", viewModel.uiState.value.password)
    }

    @Test
    fun `confirm password change updates state`() {
        viewModel.onIntent(RegisterContract.Intent.ConfirmPasswordChanged("password123"))
        assertEquals("password123", viewModel.uiState.value.confirmPassword)
    }

    @Test
    fun `toggle password visibility updates state`() {
        assertEquals(false, viewModel.uiState.value.isPasswordVisible)
        viewModel.onIntent(RegisterContract.Intent.TogglePasswordVisibility)
        assertEquals(true, viewModel.uiState.value.isPasswordVisible)
    }

    @Test
    fun `toggle confirm password visibility updates state`() {
        assertEquals(false, viewModel.uiState.value.isConfirmPasswordVisible)
        viewModel.onIntent(RegisterContract.Intent.ToggleConfirmPasswordVisibility)
        assertEquals(true, viewModel.uiState.value.isConfirmPasswordVisible)
    }

    @Test
    fun `register with empty name shows error`() {
        viewModel.onIntent(RegisterContract.Intent.RegisterClicked)
        assertEquals(R.string.auth_register_error_invalid_name, viewModel.uiState.value.nameError)
    }

    @Test
    fun `register with invalid email shows error`() {
        viewModel.onIntent(RegisterContract.Intent.NameChanged("John"))
        viewModel.onIntent(RegisterContract.Intent.EmailChanged("invalid-email"))
        viewModel.onIntent(RegisterContract.Intent.RegisterClicked)
        assertEquals(R.string.auth_login_error_invalid_email, viewModel.uiState.value.emailError)
    }

    @Test
    fun `register with short password shows error`() {
        viewModel.onIntent(RegisterContract.Intent.NameChanged("John"))
        viewModel.onIntent(RegisterContract.Intent.EmailChanged("test@example.com"))
        viewModel.onIntent(RegisterContract.Intent.PasswordChanged("short"))
        viewModel.onIntent(RegisterContract.Intent.RegisterClicked)
        assertEquals(R.string.auth_register_error_password_too_short, viewModel.uiState.value.passwordError)
    }

    @Test
    fun `register with weak complexity password shows error`() {
        viewModel.onIntent(RegisterContract.Intent.NameChanged("John"))
        viewModel.onIntent(RegisterContract.Intent.EmailChanged("test@example.com"))
        viewModel.onIntent(RegisterContract.Intent.PasswordChanged("Password123")) // No special char
        viewModel.onIntent(RegisterContract.Intent.RegisterClicked)
        assertEquals(R.string.auth_register_error_password_weak_complexity, viewModel.uiState.value.passwordError)
    }

    @Test
    fun `register with password mismatch shows error`() {
        viewModel.onIntent(RegisterContract.Intent.NameChanged("John"))
        viewModel.onIntent(RegisterContract.Intent.EmailChanged("test@example.com"))
        viewModel.onIntent(RegisterContract.Intent.PasswordChanged("Password@123"))
        viewModel.onIntent(RegisterContract.Intent.ConfirmPasswordChanged("different123"))
        viewModel.onIntent(RegisterContract.Intent.RegisterClicked)
        assertEquals(R.string.auth_register_error_password_mismatch, viewModel.uiState.value.confirmPasswordError)
    }

    @Test
    fun `successful registration navigates to home`() = runTest {
        viewModel.onIntent(RegisterContract.Intent.NameChanged("John Doe"))
        viewModel.onIntent(RegisterContract.Intent.EmailChanged("test@example.com"))
        viewModel.onIntent(RegisterContract.Intent.PasswordChanged("Password@123"))
        viewModel.onIntent(RegisterContract.Intent.ConfirmPasswordChanged("Password@123"))

        viewModel.effect.test {
            viewModel.onIntent(RegisterContract.Intent.RegisterClicked)
            runCurrent()

            assertEquals(true, viewModel.uiState.value.isLoading)
            advanceTimeBy(1501)
            runCurrent()

            assertEquals(false, viewModel.uiState.value.isLoading)
            assertEquals(true, viewModel.uiState.value.isSuccess)

            advanceTimeBy(2001)
            runCurrent()
            assertEquals(RegisterContract.Effect.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `login clicked sends navigate to login effect`() = runTest {
        viewModel.effect.test {
            viewModel.onIntent(RegisterContract.Intent.LoginClicked)
            assertEquals(RegisterContract.Effect.NavigateToLogin, awaitItem())
        }
    }
}
