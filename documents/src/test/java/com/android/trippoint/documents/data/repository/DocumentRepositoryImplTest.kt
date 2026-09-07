package com.android.trippoint.documents.data.repository

import com.android.trippoint.documents.domain.model.DocumentType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DocumentRepositoryImplTest {

    private lateinit var repository: DocumentRepositoryImpl

    @Before
    fun setUp() {
        repository = DocumentRepositoryImpl()
    }

    @Test
    fun `getDocuments with no filters returns all documents`() = runTest {
        val result = repository.getDocuments()
        assertTrue(result.isSuccess)
        assertEquals(4, result.getOrThrow().size)
    }

    @Test
    fun `getDocuments with type filter returns filtered documents`() = runTest {
        val result = repository.getDocuments(type = DocumentType.PASSPORT_VISA)
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().size)
        assertTrue(result.getOrThrow().all { it.type == DocumentType.PASSPORT_VISA })
    }

    @Test
    fun `getDocuments with favorite filter returns favorite documents`() = runTest {
        val result = repository.getDocuments(isFavorite = true)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertTrue(result.getOrThrow().all { it.isFavorite })
    }

    @Test
    fun `getDocuments with recent filter returns sorted documents`() = runTest {
        val result = repository.getDocuments(isRecent = true)
        assertTrue(result.isSuccess)
        val docs = result.getOrThrow()
        assertEquals(4, docs.size)
        // Check if sorted by updatedAt descending (mock implementation uses updatedAt)
        assertEquals("d4", docs[0].id)
    }

    @Test
    fun `getDocument with valid id returns document`() = runTest {
        val result = repository.getDocument("d1")
        assertTrue(result.isSuccess)
        assertEquals("Personal Passport", result.getOrThrow().title)
    }

    @Test
    fun `getDocument with invalid id returns failure`() = runTest {
        val result = repository.getDocument("invalid")
        assertTrue(result.isFailure)
        assertEquals("Document not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `uploadDocument adds and returns new document`() = runTest {
        val result = repository.uploadDocument("New Doc", DocumentType.INSURANCE, "url", "2025-01-01")
        assertTrue(result.isSuccess)
        val newDoc = result.getOrThrow()
        assertEquals("New Doc", newDoc.title)
        assertEquals(DocumentType.INSURANCE, newDoc.type)
        
        // Verify it was added
        val allDocs = repository.getDocuments().getOrThrow()
        assertEquals(5, allDocs.size)
        assertTrue(allDocs.any { it.id == newDoc.id })
    }

    @Test
    fun `deleteDocument removes document`() = runTest {
        val result = repository.deleteDocument("d1")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
        
        // Verify it was removed
        val getResult = repository.getDocument("d1")
        assertTrue(getResult.isFailure)
    }

    @Test
    fun `toggleFavorite updates favorite status`() = runTest {
        // d1 is favorite=true initially
        val result = repository.toggleFavorite("d1")
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrThrow())
        
        // Verify update
        val doc = repository.getDocument("d1").getOrThrow()
        assertEquals(false, doc.isFavorite)
        
        // Toggle back
        val result2 = repository.toggleFavorite("d1")
        assertTrue(result2.isSuccess)
        assertEquals(true, result2.getOrThrow())
    }

    @Test
    fun `toggleFavorite with invalid id returns failure`() = runTest {
        val result = repository.toggleFavorite("invalid")
        assertTrue(result.isFailure)
        assertEquals("Document not found", result.exceptionOrNull()?.message)
    }
}
