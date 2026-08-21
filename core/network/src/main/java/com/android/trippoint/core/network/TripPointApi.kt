package com.android.trippoint.core.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TripPointApi {
    @POST("graphql")
    suspend fun postGraphQl(@Body request: GraphQlRequest): Response<GraphQlResponse<Map<String, Any?>>>
}

data class GraphQlResponse<T>(
    val data: T?,
    val errors: List<GraphQlError>?
)

data class GraphQlError(
    val message: String
)
