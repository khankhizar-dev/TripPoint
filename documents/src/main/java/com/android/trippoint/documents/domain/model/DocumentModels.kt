package com.android.trippoint.documents.domain.model

data class Document(
    val id: String,
    val userId: String,
    val title: String,
    val type: DocumentType,
    val fileUrl: String?,
    val fileSize: String? = null,
    val fileExtension: String? = null,
    val expiryDate: String?,
    val referenceNumber: String?,
    val notes: String?,
    val isFavorite: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

enum class DocumentType {
    PASSPORT_VISA,
    TICKETS_BOARDING,
    ID_PROOF,
    HOTEL_VOUCHER,
    INSURANCE,
    OTHER
}

data class CategoryInfo(
    val type: DocumentType,
    val name: String,
    val count: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
