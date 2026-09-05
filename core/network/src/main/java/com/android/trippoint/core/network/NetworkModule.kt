package com.android.trippoint.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8081/"
    private const val TIMEOUT_SECONDS = 60L

    fun provideTripPointApi(
        authTokenProvider: () -> String?,
        refreshTokenProvider: () -> String?,
        onTokenRefreshed: (String, String) -> Unit,
        loggingEnabled: Boolean = true
    ): TripPointApi {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                authTokenProvider()?.let { token ->
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .authenticator { _, response ->
                val refreshToken = refreshTokenProvider()
                if (refreshToken != null && response.request.header("Authorization") != null) {
                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val tempClient = OkHttpClient.Builder()
                        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .build()
                    val tempRetrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(tempClient)
                        .addConverterFactory(MoshiConverterFactory.create(moshi))
                        .build()
                    val tempApi = tempRetrofit.create(TripPointApi::class.java)

                    val refreshRequest = GraphQlRequest(
                        query = """
                            mutation Refresh(${'$'}input: RefreshTokenInput!) {
                              refreshToken(input: ${'$'}input) {
                                token
                                refreshToken
                              }
                            }
                        """.trimIndent(),
                        variables = mapOf("input" to mapOf("refreshToken" to refreshToken))
                    )

                    val refreshResponse = runCatching {
                        kotlinx.coroutines.runBlocking { tempApi.postGraphQl(refreshRequest) }
                    }.getOrNull()

                    if (refreshResponse?.isSuccessful == true) {
                        val data = refreshResponse.body()?.data as? Map<String, Any>
                        val refreshData = data?.get("refreshToken") as? Map<String, String>
                        val newToken = refreshData?.get("token")
                        val newRefreshToken = refreshData?.get("refreshToken")

                        if (newToken != null && newRefreshToken != null) {
                            onTokenRefreshed(newToken, newRefreshToken)
                            return@authenticator response.request.newBuilder()
                                .header("Authorization", "Bearer $newToken")
                                .build()
                        }
                    }
                }
                null
            }
            .apply {
                if (loggingEnabled) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TripPointApi::class.java)
    }
}
