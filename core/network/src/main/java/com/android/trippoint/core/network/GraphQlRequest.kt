package com.android.trippoint.core.network

data class GraphQlRequest(
    val query: String,
    val variables: Map<String, Any?> = emptyMap()
)
