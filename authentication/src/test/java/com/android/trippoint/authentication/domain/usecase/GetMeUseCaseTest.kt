package com.android.trippoint.authentication.domain.usecase

import com.android.trippoint.authentication.domain.model.User
import com.android.trippoint.authentication.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMeUseCaseTest {

    private val repository: AuthRepository = mockk()
    private val useCase = GetMeUseCase(repository)

    @Test
    fun `invoke calls repository getMe`() = runBlocking {
        val user = User("1", "test@example.com", "John", "Doe")
        coEvery { repository.getMe() } returns Result.success(user)

        val result = useCase()

        assertEquals(Result.success(user), result)
    }
}
