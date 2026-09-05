package com.android.trippoint.ui.settings

import app.cash.turbine.test
import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.authentication.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
class EditProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: EditProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { authRepository.getMe() } returns Result.success(null)
        viewModel = EditProfileViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserProfile success updates state`() = runTest {
        val user = User("1", "test@example.com", "John", "Doe", phoneNumber = "123")
        coEvery { authRepository.getMe() } returns Result.success(user)

        // ViewModel loads user in init, so we re-init or call intent if it had one.
        // It calls loadUserProfile() in init.
        viewModel = EditProfileViewModel(authRepository)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals("John Doe", state.fullName)
        assertEquals("test@example.com", state.email)
        assertEquals("123", state.phone)
    }

    @Test
    fun `input changes update state`() {
        viewModel.onIntent(EditProfileContract.Intent.FullNameChanged("New Name"))
        assertEquals("New Name", viewModel.uiState.value.fullName)
    }

    @Test
    fun `saveProfile success navigates back`() = runTest {
        coEvery { authRepository.updateProfile(any()) } returns Result.success(true)

        viewModel.onIntent(EditProfileContract.Intent.FullNameChanged("John Doe"))
        
        viewModel.effect.test {
            viewModel.onIntent(EditProfileContract.Intent.SaveClicked)
            runCurrent()
            
            coVerify { authRepository.updateProfile(match { it.firstName == "John" && it.lastName == "Doe" }) }
            assertEquals(EditProfileContract.Effect.NavigateBack, awaitItem())
        }
    }
}
