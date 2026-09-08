package com.android.trippoint.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class DocumentRemoteDataSource(
    private val api: TripPointApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    suspend fun getDocuments(tripId: String, filter: DocumentFilterInput? = null): List<DocumentDto> {
        val query = """
            query GetDocuments(${'$'}tripId: ID!, ${'$'}filter: DocumentFilterInput) {
              documents(tripId: ${'$'}tripId, filter: ${'$'}filter) {
                id tripId uploadedBy name originalFileName mimeType fileSize
                category source status description documentNumber issuedBy
                issuedDate expiryDate favorite createdAt updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("tripId" to tripId, "filter" to filter))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("documents") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(DocumentDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    suspend fun getDocument(tripId: String, documentId: String): DocumentDto? {
        val query = """
            query GetDocument(${'$'}tripId: ID!, ${'$'}documentId: ID!) {
              document(tripId: ${'$'}tripId, id: ${'$'}documentId) {
                id tripId uploadedBy name originalFileName mimeType fileSize
                category source status description documentNumber issuedBy
                issuedDate expiryDate favorite createdAt updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("tripId" to tripId, "documentId" to documentId))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("document") ?: return null
        return moshi.adapter(DocumentDto::class.java).fromJsonValue(data)
    }

    suspend fun uploadDocument(
        tripId: String,
        name: String,
        category: String,
        source: String,
        file: File,
        description: String? = null,
        documentNumber: String? = null,
        issuedBy: String? = null,
        issuedDate: String? = null,
        expiryDate: String? = null
    ): DocumentDto? {
        val params = mutableMapOf(
            "tripId" to tripId.toRequestBody("text/plain".toMediaTypeOrNull()),
            "name" to name.toRequestBody("text/plain".toMediaTypeOrNull()),
            "category" to category.toRequestBody("text/plain".toMediaTypeOrNull()),
            "source" to source.toRequestBody("text/plain".toMediaTypeOrNull())
        )
        description?.let { params["description"] = it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        documentNumber?.let { params["documentNumber"] = it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        issuedBy?.let { params["issuedBy"] = it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        issuedDate?.let { params["issuedDate"] = it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        expiryDate?.let { params["expiryDate"] = it.toRequestBody("text/plain".toMediaTypeOrNull()) }

        val filePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        )

        val response = api.uploadDocument(params, filePart)
        if (response.isSuccessful && response.body() != null) {
            return moshi.adapter(DocumentDto::class.java).fromJsonValue(response.body())
        }
        return null
    }

    suspend fun updateDocument(tripId: String, documentId: String, input: UpdateDocumentInput): DocumentDto? {
        val query = """
            mutation UpdateDocument(${'$'}tripId: ID!, ${'$'}documentId: ID!, ${'$'}input: UpdateDocumentInput!) {
              updateDocument(tripId: ${'$'}tripId, id: ${'$'}documentId, input: ${'$'}input) {
                id name category description documentNumber issuedBy issuedDate expiryDate updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(
            query = query,
            variables = mapOf("tripId" to tripId, "documentId" to documentId, "input" to input)
        )
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateDocument") ?: return null
        return moshi.adapter(DocumentDto::class.java).fromJsonValue(data)
    }

    suspend fun favoriteDocument(tripId: String, documentId: String, favorite: Boolean): DocumentDto? {
        val query = """
            mutation FavoriteDocument(${'$'}tripId: ID!, ${'$'}documentId: ID!, ${'$'}favorite: Boolean!) {
              favoriteDocument(tripId: ${'$'}tripId, id: ${'$'}documentId, favorite: ${'$'}favorite) {
                id name favorite
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(
            query = query,
            variables = mapOf("tripId" to tripId, "documentId" to documentId, "favorite" to favorite)
        )
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("favoriteDocument") ?: return null
        return moshi.adapter(DocumentDto::class.java).fromJsonValue(data)
    }

    suspend fun trashDocument(tripId: String, documentId: String): DocumentDto? {
        val query = """
            mutation TrashDocument(${'$'}tripId: ID!, ${'$'}documentId: ID!) {
              trashDocument(tripId: ${'$'}tripId, id: ${'$'}documentId) {
                id name status
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(
            query = query,
            variables = mapOf("tripId" to tripId, "documentId" to documentId)
        )
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("trashDocument") ?: return null
        return moshi.adapter(DocumentDto::class.java).fromJsonValue(data)
    }

    suspend fun restoreDocument(tripId: String, documentId: String): DocumentDto? {
        val query = """
            mutation RestoreDocument(${'$'}tripId: ID!, ${'$'}documentId: ID!) {
              restoreDocument(tripId: ${'$'}tripId, id: ${'$'}documentId) {
                id name status
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(
            query = query,
            variables = mapOf("tripId" to tripId, "documentId" to documentId)
        )
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("restoreDocument") ?: return null
        return moshi.adapter(DocumentDto::class.java).fromJsonValue(data)
    }

    suspend fun permanentlyDeleteDocument(tripId: String, documentId: String): Boolean {
        val query = """
            mutation PermanentlyDeleteDocument(${'$'}tripId: ID!, ${'$'}documentId: ID!) {
              permanentlyDeleteDocument(tripId: ${'$'}tripId, id: ${'$'}documentId)
            }
        """.trimIndent()
        val request = GraphQlRequest(
            query = query,
            variables = mapOf("tripId" to tripId, "documentId" to documentId)
        )
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("permanentlyDeleteDocument") as? Boolean ?: false
    }
}

data class DocumentDto(
    val id: String,
    val tripId: String,
    val uploadedBy: String,
    val name: String,
    val originalFileName: String,
    val mimeType: String,
    val fileSize: Long,
    val category: String,
    val source: String,
    val status: String,
    val description: String? = null,
    val documentNumber: String? = null,
    val issuedBy: String? = null,
    val issuedDate: String? = null,
    val expiryDate: String? = null,
    val favorite: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

data class DocumentFilterInput(
    val category: String? = null,
    val includeTrashed: Boolean? = null,
    val onlyFavorites: Boolean? = null,
    val onlyMine: Boolean? = null
)

data class UpdateDocumentInput(
    val name: String? = null,
    val category: String? = null,
    val description: String? = null,
    val documentNumber: String? = null,
    val issuedBy: String? = null,
    val issuedDate: String? = null,
    val expiryDate: String? = null
)
