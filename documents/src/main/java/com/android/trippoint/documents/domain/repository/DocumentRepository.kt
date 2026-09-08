package com.android.trippoint.documents.domain.repository

import com.android.trippoint.documents.domain.model.Document
import com.android.trippoint.documents.domain.model.DocumentType
import com.android.trippoint.core.network.DocumentFilterInput
import com.android.trippoint.core.network.UpdateDocumentInput
import java.io.File

interface DocumentRepository {
    suspend fun getDocuments(tripId: String, filter: DocumentFilterInput? = null): Result<List<Document>>
    
    suspend fun getDocument(tripId: String, documentId: String): Result<Document>
    
    suspend fun uploadDocument(
        tripId: String,
        name: String,
        category: DocumentType,
        source: String,
        file: File,
        description: String? = null,
        documentNumber: String? = null,
        issuedBy: String? = null,
        issuedDate: String? = null,
        expiryDate: String? = null
    ): Result<Document>

    suspend fun updateDocument(tripId: String, documentId: String, input: UpdateDocumentInput): Result<Document>

    suspend fun toggleFavorite(tripId: String, documentId: String, favorite: Boolean): Result<Document>

    suspend fun trashDocument(tripId: String, documentId: String): Result<Document>

    suspend fun restoreDocument(tripId: String, documentId: String): Result<Document>

    suspend fun permanentlyDeleteDocument(tripId: String, documentId: String): Result<Boolean>
}
