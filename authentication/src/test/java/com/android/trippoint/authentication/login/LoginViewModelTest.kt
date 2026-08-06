package com.android.trippoint.authentication.login

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
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val preferencesManager: PreferencesManager = mockk(relaxed = true)
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(preferencesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertNull(state.emailError)
        assertNull(state.passwordError)
    }

    @Test
    fun `email change updates state`() {
        viewModel.onIntent(LoginContract.Intent.EmailChanged("test@example.com"))
        assertEquals("test@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun `password change updates state`() {
        viewModel.onIntent(LoginContract.Intent.PasswordChanged("password123"))
        assertEquals("password123", viewModel.uiState.value.password)
    }

    @Test
    fun `toggle password visibility updates state`() {
        assertEquals(false, viewModel.uiState.value.isPasswordVisible)
        viewModel.onIntent(LoginContract.Intent.TogglePasswordVisibility)
        assertEquals(true, viewModel.uiState.value.isPasswordVisible)
    }

    @Test
    fun `login with empty email shows error`() {
        viewModel.onIntent(LoginContract.Intent.LoginClicked)
        assertEquals(R.string.auth_login_error_invalid_email, viewModel.uiState.value.emailError)
    }

    @Test
    fun `login with invalid email shows error`() {
        viewModel.onIntent(LoginContract.Intent.EmailChanged("invalid-email"))
        viewModel.onIntent(LoginContract.Intent.LoginClicked)
        assertEquals(R.string.auth_login_error_invalid_email, viewModel.uiState.value.emailError)
    }

    @Test
    fun `login with empty password shows error`() {
        viewModel.onIntent(LoginContract.Intent.EmailChanged("test@example.com"))
        viewModel.onIntent(LoginContract.Intent.LoginClicked)
        assertEquals(R.string.auth_login_error_empty_password, viewModel.uiState.value.passwordError)
    }

    @Test
    fun `successful login navigates to home`() = runTest {
        viewModel.onIntent(LoginContract.Intent.EmailChanged("test@example.com"))
        viewModel.onIntent(LoginContract.Intent.PasswordChanged("password"))
        
        viewModel.effect.test {
            viewModel.onIntent(LoginContract.Intent.LoginClicked)
            runCurrent() // Run the launched coroutine until it hits delay
            
            assertEquals(true, viewModel.uiState.value.isLoading)
            advanceTimeBy(1501)
            runCurrent() // Run the rest of the coroutine after delay

            assertEquals(false, viewModel.uiState.value.isLoading)
            assertEquals(LoginContract.Effect.NavigateToHome, awaitItem())
        }
    }
}
