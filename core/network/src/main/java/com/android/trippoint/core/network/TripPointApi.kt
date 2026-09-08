package com.android.trippoint.core.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface TripPointApi {
    @POST("graphql")
    suspend fun postGraphQl(@Body request: GraphQlRequest): Response<GraphQlResponse<Map<String, Any?>>>

    @Multipart
    @POST("api/documents/upload")
    suspend fun uploadDocument(
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: MultipartBody.Part
    ): Response<Map<String, Any?>>

    @Streaming
    @GET("api/documents/{documentId}/download")
    suspend fun downloadDocument(
        @Path("documentId") documentId: String,
        @Query("tripId") tripId: String
    ): Response<ResponseBody>
}

data class GraphQlResponse<T>(
    val data: T?,
    val errors: List<GraphQlError>?
)

data class GraphQlError(
    val message: String
)
