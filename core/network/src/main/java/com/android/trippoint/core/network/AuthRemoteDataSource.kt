package com.android.trippoint.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class AuthRemoteDataSource(
    private val api: TripPointApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    suspend fun register(input: RegisterInput): AuthResponse? {
        val query = """
            mutation Register(${'$'}input: RegisterInput!) {
              register(input: ${'$'}input) {
                user { 
                  id email firstName lastName fullName username 
                  phoneNumber dateOfBirth nationality profilePhotoUrl country 
                }
                token
                refreshToken
              }
            }
        """.trimIndent()

        val request = GraphQlRequest(
            query = query,
            variables = mapOf("input" to input)
        )

        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("register") ?: return null
        return moshi.adapter(AuthResponse::class.java).fromJsonValue(data)
    }

    suspend fun login(email: String, password: String): AuthResponse? {
        val query = """
            mutation Login(${'$'}email: String!, ${'$'}password: String!) {
              login(email: ${'$'}email, password: ${'$'}password) {
                user { 
                  id email firstName lastName fullName username 
                  phoneNumber dateOfBirth nationality profilePhotoUrl country 
                }
                token
                refreshToken
              }
            }
        """.trimIndent()

        val request = GraphQlRequest(
            query = query,
            variables = mapOf("email" to email, "password" to password)
        )

        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("login") ?: return null
        return moshi.adapter(AuthResponse::class.java).fromJsonValue(data)
    }

    suspend fun verifyEmailOtp(email: String, otp: String): Boolean {
        val query = """
            mutation VerifyEmail(${'$'}input: VerifyEmailOtpInput!) {
              verifyEmailOtp(input: ${'$'}input)
            }
        """.trimIndent()

        val request = GraphQlRequest(
            query = query,
            variables = mapOf("input" to mapOf("email" to email, "otp" to otp))
        )

        val response = api.postGraphQl(request)
        return response.body()?.data?.get("verifyEmailOtp") as? Boolean ?: false
    }

    suspend fun resendEmailOtp(): Boolean {
        val query = "mutation { resendEmailOtp }"
        val request = GraphQlRequest(query = query)
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("resendEmailOtp") as? Boolean ?: false
    }

    suspend fun requestPasswordReset(email: String): Boolean {
        val query = """
            mutation RequestReset(${'$'}email: String!) {
              requestPasswordReset(email: ${'$'}email)
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("email" to email))
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("requestPasswordReset") as? Boolean ?: false
    }

    suspend fun resetPassword(input: ResetPasswordInput): Boolean {
        val query = """
            mutation ResetPassword(${'$'}input: ResetPasswordInput!) {
              resetPassword(input: ${'$'}input)
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query, variables = mapOf("input" to input))
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("resetPassword") as? Boolean ?: false
    }

    suspend fun updateProfile(input: UpdateProfileInput): Boolean {
        val query = """
            mutation UpdateProfile(${'$'}input: UpdateProfileInput!) {
              updateProfile(input: ${'$'}input) {
                id
                email
                firstName
                lastName
                fullName
                username
                phoneNumber
                dateOfBirth
                nationality
                profilePhotoUrl
                country
              }
            }
        """.trimIndent()

        val request = GraphQlRequest(
            query = query,
            variables = mapOf("input" to input)
        )

        val response = api.postGraphQl(request)
        val body = response.body()
        if (body?.errors != null && body.errors.isNotEmpty()) {
            android.util.Log.e("AuthRemoteDataSource", "GraphQL Errors: ${body.errors}")
        }
        return response.isSuccessful && body?.data?.get("updateProfile") != null
    }

    suspend fun updatePreferences(input: UpdatePreferencesInput): UserPreferences? {
        val query = """
            mutation UpdatePreferences(${'$'}input: UpdatePreferencesInput!) {
              updatePreferences(input: ${'$'}input) {
                currency
                language
                dateFormat
                units
                theme
                timezone
              }
            }
        """.trimIndent()

        val request = GraphQlRequest(
            query = query,
            variables = mapOf("input" to input)
        )

        val response = api.postGraphQl(request)
        val body = response.body()
        if (body?.errors != null && body.errors.isNotEmpty()) {
            android.util.Log.e("AuthRemoteDataSource", "GraphQL Errors: ${body.errors}")
        }
        val data = body?.data?.get("updatePreferences") ?: return null
        return moshi.adapter(UserPreferences::class.java).fromJsonValue(data)
    }

    suspend fun getMe(): User? {
        val query = """
            query GetMyProfile {
              me {
                id
                email
                firstName
                lastName
                fullName
                username
                phoneNumber
                dateOfBirth
                nationality
                profilePhotoUrl
                country
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query)
        val response = api.postGraphQl(request)
        val body = response.body()
        if (body?.errors != null && body.errors.isNotEmpty()) {
            android.util.Log.e("AuthRemoteDataSource", "GraphQL Errors: ${body.errors}")
        }
        val data = body?.data?.get("me") ?: return null
        return moshi.adapter(User::class.java).fromJsonValue(data)
    }

    suspend fun getMyPreferences(): UserPreferences? {
        val query = """
            query GetMyPreferences {
              myPreferences {
                currency
                language
                dateFormat
                units
                theme
                timezone
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("myPreferences") ?: return null
        return moshi.adapter(UserPreferences::class.java).fromJsonValue(data)
    }

    suspend fun getUserDevices(): List<UserDevice> {
        val query = """
            query GetUserDevices {
              userDevices {
                id
                deviceName
                platform
                appVersion
                lastLoginAt
                createdAt
              }
            }
        """.trimIndent()
        val request = GraphQlRequest(query = query)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("userDevices") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(UserDevice::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    suspend fun changePassword(current: String, new: String): Boolean {
        val query = """
            mutation ChangePassword(${'$'}current: String!, ${'$'}new: String!) {
              changePassword(currentPassword: ${'$'}current, newPassword: ${'$'}new)
            }
        """.trimIndent()
        val request = GraphQlRequest(
            query = query,
            variables = mapOf("current" to current, "new" to new)
        )
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("changePassword") as? Boolean ?: false
    }

    suspend fun logout(): Boolean {
        val query = "mutation { logout }"
        val request = GraphQlRequest(query = query)
        val response = api.postGraphQl(request)
        return response.body()?.data?.get("logout") as? Boolean ?: false
    }
}

data class User(
    val id: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val fullName: String? = null,
    val username: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val nationality: String? = null,
    val profilePhotoUrl: String? = null,
    val country: String? = null
)

data class UserPreferences(
    val currency: String?,
    val language: String?,
    val dateFormat: String?,
    val units: String?,
    val theme: String?,
    val timezone: String?
)

data class UserDevice(
    val id: String,
    val deviceName: String?,
    val platform: String?,
    val appVersion: String?,
    val lastLoginAt: String?,
    val createdAt: String
)

data class AuthResponse(
    val user: User,
    val token: String,
    val refreshToken: String
)

data class RegisterInput(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

data class UpdateProfileInput(
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val country: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val nationality: String? = null,
    val profilePhotoUrl: String? = null
)

data class UpdatePreferencesInput(
    val currency: String? = null,
    val language: String? = null,
    val dateFormat: String? = null,
    val units: String? = null,
    val theme: String? = null,
    val timezone: String? = null
)

data class ResetPasswordInput(
    val email: String,
    val otp: String,
    val newPassword: String
)
