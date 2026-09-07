package com.android.trippoint.documents.data.repository

import com.android.trippoint.documents.domain.model.Document
import com.android.trippoint.documents.domain.model.DocumentType
import com.android.trippoint.documents.domain.repository.DocumentRepository

class DocumentRepositoryImpl : DocumentRepository {
    
    private val mockDocuments = mutableListOf(
        Document(
            id = "d1",
            userId = "u1",
            title = "Personal Passport",
            type = DocumentType.PASSPORT_VISA,
            fileUrl = "https://example.com/passport.pdf",
            fileSize = "1.2 MB",
            fileExtension = "pdf",
            expiryDate = "2030-12-31",
            referenceNumber = "A1234567",
            notes = "Stored in safe",
            isFavorite = true,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        ),
        Document(
            id = "d2",
            userId = "u1",
            title = "Thailand Visa",
            type = DocumentType.PASSPORT_VISA,
            fileUrl = "https://example.com/visa.pdf",
            fileSize = "0.8 MB",
            fileExtension = "pdf",
            expiryDate = "2024-11-01",
            referenceNumber = "V9876543",
            notes = "Single entry",
            createdAt = "2024-02-01",
            updatedAt = "2024-02-01"
        ),
        Document(
            id = "d3",
            userId = "u1",
            title = "Flight Ticket - Bali",
            type = DocumentType.TICKET_BOARDING,
            fileUrl = "https://example.com/ticket.pdf",
            fileSize = "1.5 MB",
            fileExtension = "pdf",
            expiryDate = null,
            referenceNumber = "PNR123",
            notes = null,
            createdAt = "2024-05-10",
            updatedAt = "2024-05-10"
        ),
        Document(
            id = "d4",
            userId = "u1",
            title = "Hotel Voucher",
            type = DocumentType.HOTEL_VOUCHERS,
            fileUrl = "https://example.com/hotel.pdf",
            fileSize = "0.5 MB",
            fileExtension = "pdf",
            expiryDate = null,
            referenceNumber = "HTL999",
            notes = null,
            createdAt = "2024-05-11",
            updatedAt = "2024-05-11"
        )
    )

    override suspend fun getDocuments(
        type: DocumentType?,
        isFavorite: Boolean?,
        isShared: Boolean?,
        isRecent: Boolean?
    ): Result<List<Document>> {
        var filtered = mockDocuments.toList()
        if (type != null) filtered = filtered.filter { it.type == type }
        if (isFavorite != null) filtered = filtered.filter { it.isFavorite == isFavorite }
        // shared and recent logic would be more complex, but for mock:
        if (isRecent == true) filtered = filtered.sortedByDescending { it.updatedAt }.take(5)
        
        return Result.success(filtered)
    }

    override suspend fun getDocument(id: String): Result<Document> {
        val doc = mockDocuments.find { it.id == id }
        return if (doc != null) Result.success(doc)
        else Result.failure(Exception("Document not found"))
    }

    override suspend fun uploadDocument(
        title: String, 
        type: DocumentType, 
        fileUrl: String, 
        expiryDate: String?
    ): Result<Document> {
        val newDoc = Document(
            id = "d${mockDocuments.size + 1}",
            userId = "u1",
            title = title,
            type = type,
            fileUrl = fileUrl,
            fileSize = "0.0 MB",
            fileExtension = "pdf",
            expiryDate = expiryDate,
            referenceNumber = null,
            notes = null,
            createdAt = "2024-09-06",
            updatedAt = "2024-09-06"
        )
        mockDocuments.add(newDoc)
        return Result.success(newDoc)
    }

    override suspend fun deleteDocument(id: String): Result<Boolean> {
        mockDocuments.removeIf { it.id == id }
        return Result.success(true)
    }

    override suspend fun toggleFavorite(id: String): Result<Boolean> {
        val index = mockDocuments.indexOfFirst { it.id == id }
        if (index != -1) {
            val doc = mockDocuments[index]
            mockDocuments[index] = doc.copy(isFavorite = !doc.isFavorite)
            return Result.success(mockDocuments[index].isFavorite)
        }
        return Result.failure(Exception("Document not found"))
    }
}
