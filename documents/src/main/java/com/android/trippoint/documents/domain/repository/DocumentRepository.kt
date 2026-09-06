package com.android.trippoint.documents.domain.repository

import com.android.trippoint.documents.domain.model.Document
import com.android.trippoint.documents.domain.model.DocumentType

interface DocumentRepository {
    suspend fun getDocuments(
        type: DocumentType? = null,
        isFavorite: Boolean? = null,
        isShared: Boolean? = null,
        isRecent: Boolean? = null
    ): Result<List<Document>>
    suspend fun getDocument(id: String): Result<Document>
    suspend fun uploadDocument(
        title: String,
        type: DocumentType,
        fileUrl: String,
        expiryDate: String?
    ): Result<Document>
    suspend fun deleteDocument(id: String): Result<Boolean>
    suspend fun toggleFavorite(id: String): Result<Boolean>
}
