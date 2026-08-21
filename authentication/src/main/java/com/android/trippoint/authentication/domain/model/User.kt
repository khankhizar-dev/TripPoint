package com.android.trippoint.authentication.domain.model

data class User(
    val id: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val username: String? = null,
    val profilePhotoUrl: String? = null,
    val country: String? = null,
    val currency: String? = null,
    val language: String? = null,
    val timezone: String? = null
)
