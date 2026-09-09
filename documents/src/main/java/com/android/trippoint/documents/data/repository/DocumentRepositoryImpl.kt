package com.android.trippoint.documents.data.repository

import com.android.trippoint.documents.domain.model.Document
import com.android.trippoint.documents.domain.model.DocumentType
import com.android.trippoint.documents.domain.repository.DocumentRepository
import com.android.trippoint.core.network.DocumentRemoteDataSource
import com.android.trippoint.core.network.DocumentDto
import com.android.trippoint.core.network.DocumentFilterInput
import com.android.trippoint.core.network.UpdateDocumentInput
import java.io.File

class DocumentRepositoryImpl(
    private val remoteDataSource: DocumentRemoteDataSource
) : DocumentRepository {

    override suspend fun getDocuments(tripId: String, filter: DocumentFilterInput?): Result<List<Document>> {
        return try {
            val dtos = remoteDataSource.getDocuments(tripId, filter)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDocument(tripId: String, documentId: String): Result<Document> {
        return try {
            val dto = remoteDataSource.getDocument(tripId, documentId)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Document not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadDocument(
        tripId: String,
        name: String,
        category: DocumentType,
        source: String,
        file: File,
        description: String?,
        documentNumber: String?,
        issuedBy: String?,
        issuedDate: String?,
        expiryDate: String?
    ): Result<Document> {
        return try {
            val dto = remoteDataSource.uploadDocument(
                tripId, name, category.name, source, file,
                description, documentNumber, issuedBy, issuedDate, expiryDate
            )
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Upload failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDocument(
        tripId: String,
        documentId: String,
        input: UpdateDocumentInput
    ): Result<Document> {
        return try {
            val dto = remoteDataSource.updateDocument(tripId, documentId, input)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Update failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(tripId: String, documentId: String, favorite: Boolean): Result<Document> {
        return try {
            val dto = remoteDataSource.favoriteDocument(tripId, documentId, favorite)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Favorite toggle failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun trashDocument(tripId: String, documentId: String): Result<Document> {
        return try {
            val dto = remoteDataSource.trashDocument(tripId, documentId)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Trash failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restoreDocument(tripId: String, documentId: String): Result<Document> {
        return try {
            val dto = remoteDataSource.restoreDocument(tripId, documentId)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Restore failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun permanentlyDeleteDocument(tripId: String, documentId: String): Result<Boolean> {
        return try {
            val success = remoteDataSource.permanentlyDeleteDocument(tripId, documentId)
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun DocumentDto.toDomain(): Document {
        return Document(
            id = id,
            userId = uploadedBy,
            title = name,
            type = try { DocumentType.valueOf(category) } catch (_: Exception) { DocumentType.OTHER },
            fileUrl = null, // Backend should provide a way to construct this or a separate download call
            fileSize = "${fileSize / 1024} KB",
            fileExtension = mimeType.substringAfterLast("/"),
            expiryDate = expiryDate,
            referenceNumber = documentNumber,
            notes = description,
            isFavorite = favorite,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
