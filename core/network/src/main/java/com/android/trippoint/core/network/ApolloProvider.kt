package com.android.trippoint.core.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object ApolloProvider {
    private const val BASE_URL = "http://10.0.2.2:8081/graphql" // 10.0.2.2 is localhost for Android Emulator

    fun provideApolloClient(
        authTokenProvider: () -> String?,
        loggingEnabled: Boolean = true
    ): ApolloClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                authTokenProvider()?.let { token ->
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .apply {
                if (loggingEnabled) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
            .build()

        return ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .build()
    }
}
