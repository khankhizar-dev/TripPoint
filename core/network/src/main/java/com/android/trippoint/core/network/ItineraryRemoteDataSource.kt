package com.android.trippoint.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ItineraryRemoteDataSource(
    private val api: TripPointApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    suspend fun getItineraryDays(tripId: String): List<ItineraryDayDto> {
        val query = """
            query GetItineraryDays(${'$'}tripId: ID!) {
              itineraryDays(tripId: ${'$'}tripId) {
                id tripId dayNumber date title notes createdAt updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("tripId" to tripId))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("itineraryDays") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(ItineraryDayDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    suspend fun getItineraryDay(tripId: String, dayNumber: Int): ItineraryDayDto? {
        val query = """
            query GetItineraryDay(${'$'}tripId: ID!, ${'$'}dayNumber: Int!) {
              itineraryDay(tripId: ${'$'}tripId, dayNumber: ${'$'}dayNumber) {
                id tripId dayNumber date title notes createdAt updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("tripId" to tripId, "dayNumber" to dayNumber))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("itineraryDay") ?: return null
        return moshi.adapter(ItineraryDayDto::class.java).fromJsonValue(data)
    }

    suspend fun createItineraryDay(tripId: String, input: CreateItineraryDayInput): ItineraryDayDto? {
        val query = """
            mutation CreateItineraryDay(${'$'}tripId: ID!, ${'$'}input: CreateItineraryDayInput!) {
              createItineraryDay(tripId: ${'$'}tripId, input: ${'$'}input) {
                id tripId dayNumber date title notes createdAt updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("tripId" to tripId, "input" to input))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("createItineraryDay") ?: return null
        return moshi.adapter(ItineraryDayDto::class.java).fromJsonValue(data)
    }

    suspend fun updateItineraryDay(tripId: String, dayNumber: Int, input: UpdateItineraryDayInput): ItineraryDayDto? {
        val query = """
            mutation UpdateItineraryDay(${'$'}tripId: ID!, ${'$'}dayNumber: Int!, ${'$'}input: UpdateItineraryDayInput!) {
              updateItineraryDay(tripId: ${'$'}tripId, dayNumber: ${'$'}dayNumber, input: ${'$'}input) {
                id tripId dayNumber date title notes createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "dayNumber" to dayNumber, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateItineraryDay") ?: return null
        return moshi.adapter(ItineraryDayDto::class.java).fromJsonValue(data)
    }

    suspend fun deleteItineraryDay(tripId: String, dayNumber: Int): Boolean {
        val query = """
            mutation DeleteItineraryDay(${'$'}tripId: ID!, ${'$'}dayNumber: Int!) {
              deleteItineraryDay(tripId: ${'$'}tripId, dayNumber: ${'$'}dayNumber)
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("tripId" to tripId, "dayNumber" to dayNumber))
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("deleteItineraryDay") as? Boolean ?: false
    }

    suspend fun getItineraryActivities(tripId: String, itineraryDayId: String): List<ItineraryActivityDto> {
        val query = """
            query GetItineraryActivities(${'$'}tripId: ID!, ${'$'}itineraryDayId: ID!) {
              itineraryActivities(tripId: ${'$'}tripId, itineraryDayId: ${'$'}itineraryDayId) {
                id itineraryDayId title description type startTime endTime location latitude longitude
                sortOrder completed createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "itineraryDayId" to itineraryDayId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("itineraryActivities") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(ItineraryActivityDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    suspend fun getItineraryActivity(
        tripId: String,
        itineraryDayId: String,
        activityId: String
    ): ItineraryActivityDto? {
        val query = """
            query GetItineraryActivity(${'$'}tripId: ID!, ${'$'}itineraryDayId: ID!, ${'$'}activityId: ID!) {
              itineraryActivity(tripId: ${'$'}tripId, itineraryDayId: ${'$'}itineraryDayId, activityId: ${'$'}activityId) {
                id itineraryDayId title description type startTime endTime location latitude longitude
                sortOrder completed createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "itineraryDayId" to itineraryDayId, "activityId" to activityId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("itineraryActivity") ?: return null
        return moshi.adapter(ItineraryActivityDto::class.java).fromJsonValue(data)
    }

    suspend fun createItineraryActivity(
        tripId: String,
        itineraryDayId: String,
        input: CreateItineraryActivityInput
    ): ItineraryActivityDto? {
        val query = """
            mutation CreateItineraryActivity(
                ${'$'}tripId: ID!, ${'$'}itineraryDayId: ID!, ${'$'}input: CreateItineraryActivityInput!
            ) {
              createItineraryActivity(tripId: ${'$'}tripId, itineraryDayId: ${'$'}itineraryDayId, input: ${'$'}input) {
                id itineraryDayId title description type startTime endTime location latitude longitude
                sortOrder completed createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "itineraryDayId" to itineraryDayId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("createItineraryActivity") ?: return null
        return moshi.adapter(ItineraryActivityDto::class.java).fromJsonValue(data)
    }

    suspend fun updateItineraryActivity(
        tripId: String,
        itineraryDayId: String,
        activityId: String,
        input: UpdateItineraryActivityInput
    ): ItineraryActivityDto? {
        val query = """
            mutation UpdateItineraryActivity(
                ${'$'}tripId: ID!, ${'$'}itineraryDayId: ID!, ${'$'}activityId: ID!, 
                ${'$'}input: UpdateItineraryActivityInput!
            ) {
              updateItineraryActivity(
                  tripId: ${'$'}tripId, itineraryDayId: ${'$'}itineraryDayId, 
                  activityId: ${'$'}activityId, input: ${'$'}input
              ) {
                id itineraryDayId title description type startTime endTime location latitude longitude
                sortOrder completed createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf(
            "tripId" to tripId,
            "itineraryDayId" to itineraryDayId,
            "activityId" to activityId,
            "input" to input
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateItineraryActivity") ?: return null
        return moshi.adapter(ItineraryActivityDto::class.java).fromJsonValue(data)
    }

    suspend fun markItineraryActivityCompleted(
        tripId: String,
        itineraryDayId: String,
        activityId: String,
        completed: Boolean
    ): ItineraryActivityDto? {
        val query = """
            mutation MarkItineraryActivityCompleted(
                ${'$'}tripId: ID!, ${'$'}itineraryDayId: ID!, 
                ${'$'}activityId: ID!, ${'$'}completed: Boolean!
            ) {
              markItineraryActivityCompleted(
                  tripId: ${'$'}tripId, itineraryDayId: ${'$'}itineraryDayId, 
                  activityId: ${'$'}activityId, completed: ${'$'}completed
              ) {
                id completed updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf(
            "tripId" to tripId,
            "itineraryDayId" to itineraryDayId,
            "activityId" to activityId,
            "completed" to completed
        )
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("markItineraryActivityCompleted") ?: return null
        return moshi.adapter(ItineraryActivityDto::class.java).fromJsonValue(data)
    }

    suspend fun deleteItineraryActivity(tripId: String, itineraryDayId: String, activityId: String): Boolean {
        val query = """
            mutation DeleteItineraryActivity(${'$'}tripId: ID!, ${'$'}itineraryDayId: ID!, ${'$'}activityId: ID!) {
              deleteItineraryActivity(
                  tripId: ${'$'}tripId, itineraryDayId: ${'$'}itineraryDayId, activityId: ${'$'}activityId
              )
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "itineraryDayId" to itineraryDayId, "activityId" to activityId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("deleteItineraryActivity") as? Boolean ?: false
    }
}

data class ItineraryDayDto(
    val id: String,
    val tripId: String,
    val dayNumber: Int,
    val date: String,
    val title: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
)

data class ItineraryActivityDto(
    val id: String,
    val itineraryDayId: String,
    val title: String,
    val description: String?,
    val type: String,
    val startTime: String,
    val endTime: String?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val sortOrder: Int,
    val completed: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class CreateItineraryDayInput(
    val dayNumber: Int,
    val date: String,
    val title: String?,
    val notes: String?
)

data class UpdateItineraryDayInput(
    val title: String?,
    val notes: String?
)

data class CreateItineraryActivityInput(
    val title: String,
    val description: String?,
    val type: String,
    val startTime: String,
    val endTime: String?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val sortOrder: Int
)

data class UpdateItineraryActivityInput(
    val title: String? = null,
    val description: String? = null,
    val type: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val sortOrder: Int? = null,
    val completed: Boolean? = null
)
