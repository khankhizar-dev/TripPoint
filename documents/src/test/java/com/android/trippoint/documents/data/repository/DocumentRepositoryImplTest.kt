package com.android.trippoint.documents.data.repository

import com.android.trippoint.core.network.DocumentRemoteDataSource
import com.android.trippoint.core.network.DocumentDto
import com.android.trippoint.documents.domain.model.DocumentType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class DocumentRepositoryImplTest {

    private val remoteDataSource: DocumentRemoteDataSource = mockk()
    private lateinit var repository: DocumentRepositoryImpl

    private val dummyDto = DocumentDto(
        id = "d1",
        tripId = "t1",
        uploadedBy = "u1",
        name = "Passport",
        originalFileName = "pass.pdf",
        mimeType = "application/pdf",
        fileSize = 1024,
        category = "PASSPORT_VISA",
        source = "DEVICE",
        status = "ACTIVE",
        createdAt = "now",
        updatedAt = "now"
    )

    @Before
    fun setUp() {
        repository = DocumentRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getDocuments returns mapped domain data`() = runTest {
        coEvery { remoteDataSource.getDocuments("t1", any()) } returns listOf(dummyDto)

        val result = repository.getDocuments("t1")
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals("Passport", result.getOrThrow().first().title)
    }

    @Test
    fun `getDocument returns mapped domain data`() = runTest {
        coEvery { remoteDataSource.getDocument("t1", "d1") } returns dummyDto

        val result = repository.getDocument("t1", "d1")
        
        assertTrue(result.isSuccess)
        assertEquals("Passport", result.getOrThrow().title)
    }

    @Test
    fun `uploadDocument returns success`() = runTest {
        val file = File("path")
        coEvery { 
            remoteDataSource.uploadDocument(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) 
        } returns dummyDto

        val result = repository.uploadDocument("t1", "Name", DocumentType.PASSPORT_VISA, "DEVICE", file)
        
        assertTrue(result.isSuccess)
        assertEquals("Passport", result.getOrThrow().title)
    }

    @Test
    fun `trashDocument returns success`() = runTest {
        coEvery { remoteDataSource.trashDocument("t1", "d1") } returns dummyDto

        val result = repository.trashDocument("t1", "d1")
        
        assertTrue(result.isSuccess)
    }

    @Test
    fun `toggleFavorite returns success`() = runTest {
        coEvery { remoteDataSource.favoriteDocument("t1", "d1", true) } returns dummyDto.copy(favorite = true)

        val result = repository.toggleFavorite("t1", "d1", true)
        
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrThrow().isFavorite)
    }
}
