package com.android.trippoint.authentication.domain.model

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
