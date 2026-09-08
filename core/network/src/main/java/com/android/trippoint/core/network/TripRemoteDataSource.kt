package com.android.trippoint.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class TripRemoteDataSource(
    private val api: TripPointApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    suspend fun createTrip(input: CreateTripInput): TripDto? {
        val query = """
            mutation CreateTrip(${'$'}input: CreateTripInput!) {
              createTrip(input: ${'$'}input) {
                id ownerId name destination startDate endDate status progress travelers createdAt updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("input" to input))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("createTrip") ?: return null
        return moshi.adapter(TripDto::class.java).fromJsonValue(data)
    }

    suspend fun getTrips(filter: TripFilterInput? = null): List<TripDto> {
        val query = """
            query GetTrips(${'$'}filter: TripFilterInput) {
              trips(filter: ${'$'}filter) {
                id ownerId name destination startDate endDate status progress travelers createdAt updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("filter" to filter))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("trips") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(TripDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    suspend fun getTrip(tripId: String): TripDto? {
        val query = """
            query GetTrip(${'$'}id: ID!) {
              trip(id: ${'$'}id) {
                id ownerId name destination startDate endDate status progress travelers createdAt updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("id" to tripId))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("trip") ?: return null
        return moshi.adapter(TripDto::class.java).fromJsonValue(data)
    }

    suspend fun updateTrip(tripId: String, input: UpdateTripInput): TripDto? {
        val query = """
            mutation UpdateTrip(${'$'}id: ID!, ${'$'}input: UpdateTripInput!) {
              updateTrip(id: ${'$'}id, input: ${'$'}input) {
                id ownerId name destination startDate endDate status progress travelers createdAt updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("id" to tripId, "input" to input))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateTrip") ?: return null
        return moshi.adapter(TripDto::class.java).fromJsonValue(data)
    }

    suspend fun archiveTrip(tripId: String): Boolean {
        val query = """
            mutation ArchiveTrip(${'$'}id: ID!) {
              archiveTrip(id: ${'$'}id)
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("id" to tripId))
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("archiveTrip") as? Boolean ?: false
    }

    suspend fun restoreTrip(tripId: String): TripDto? {
        val query = """
            mutation RestoreTrip(${'$'}id: ID!) {
              restoreTrip(id: ${'$'}id) {
                id ownerId name destination startDate endDate status progress travelers createdAt updatedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("id" to tripId))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("restoreTrip") ?: return null
        return moshi.adapter(TripDto::class.java).fromJsonValue(data)
    }

    suspend fun deleteTrip(tripId: String): Boolean {
        val query = """
            mutation DeleteTrip(${'$'}id: ID!) {
              deleteTrip(id: ${'$'}id)
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("id" to tripId))
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("deleteTrip") as? Boolean ?: false
    }

    suspend fun getTripMembers(tripId: String): List<TripMemberDto> {
        val query = """
            query GetTripMembers(${'$'}tripId: ID!) {
              tripMembers(tripId: ${'$'}tripId) {
                id tripId userId role status invitedAt joinedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("tripId" to tripId))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("tripMembers") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(TripMemberDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    suspend fun inviteTripMember(tripId: String, input: InviteTripMemberInput): TripMemberDto? {
        val query = """
            mutation InviteTripMember(${'$'}tripId: ID!, ${'$'}input: InviteTripMemberInput!) {
              inviteTripMember(tripId: ${'$'}tripId, input: ${'$'}input) {
                id tripId userId role status invitedAt joinedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("tripId" to tripId, "input" to input))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("inviteTripMember") ?: return null
        return moshi.adapter(TripMemberDto::class.java).fromJsonValue(data)
    }

    suspend fun acceptTripInvitation(tripId: String): TripMemberDto? {
        val query = """
            mutation AcceptTripInvitation(${'$'}tripId: ID!) {
              acceptTripInvitation(tripId: ${'$'}tripId) {
                id tripId userId role status invitedAt joinedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("tripId" to tripId))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("acceptTripInvitation") ?: return null
        return moshi.adapter(TripMemberDto::class.java).fromJsonValue(data)
    }

    suspend fun declineTripInvitation(tripId: String): TripMemberDto? {
        val query = """
            mutation DeclineTripInvitation(${'$'}tripId: ID!) {
              declineTripInvitation(tripId: ${'$'}tripId) {
                id tripId userId role status invitedAt joinedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("tripId" to tripId))
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("declineTripInvitation") ?: return null
        return moshi.adapter(TripMemberDto::class.java).fromJsonValue(data)
    }

    suspend fun getMyTripInvitations(): List<TripMemberDto> {
        val query = """
            query MyTripInvitations {
              myTripInvitations {
                id tripId userId role status invitedAt joinedAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("myTripInvitations") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(TripMemberDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }
}

data class TripDto(
    val id: String,
    val ownerId: String,
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val progress: Float,
    val travelers: Int,
    val createdAt: String,
    val updatedAt: String
)

data class TripMemberDto(
    val id: String,
    val tripId: String,
    val userId: String,
    val role: String,
    val status: String,
    val invitedAt: String,
    val joinedAt: String?
)

data class CreateTripInput(
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String
)

data class UpdateTripInput(
    val name: String? = null,
    val destination: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val status: String? = null
)

data class TripFilterInput(
    val status: String? = null,
    val search: String? = null
)

data class InviteTripMemberInput(
    val email: String
)
