package com.android.trippoint.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AuthRemoteDataSourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var dataSource: AuthRemoteDataSource

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TripPointApi::class.java)
        dataSource = AuthRemoteDataSource(api)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `login returns success response`() = runBlocking {
        val jsonResponse = """
            {
              "data": {
                "login": {
                  "user": { 
                    "id": "1", 
                    "email": "test@example.com", 
                    "firstName": "John", 
                    "lastName": "Doe",
                    "username": "johndoe",
                    "profilePhotoUrl": null,
                    "country": "IN",
                    "currency": "INR",
                    "language": "EN",
                    "timezone": "IST"
                  },
                  "token": "access-token",
                  "refreshToken": "refresh-token"
                }
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse))

        val response = dataSource.login("test@example.com", "password")

        assertNotNull(response)
        assertEquals("access-token", response?.token)
        assertEquals("refresh-token", response?.refreshToken)
        assertEquals("John", response?.user?.firstName)
        assertEquals("johndoe", response?.user?.username)
        assertEquals("IN", response?.user?.country)

        val recordedRequest = mockWebServer.takeRequest()
        assertNotNull(recordedRequest.body.readUtf8().contains("mutation Login"))
    }

    @Test
    fun `updateProfile returns success`() = runBlocking {
        val jsonResponse = """
            {
              "data": {
                "updateProfile": {
                  "id": "1",
                  "email": "test@example.com",
                  "firstName": "John",
                  "lastName": "Doe",
                  "username": "johndoe",
                  "profilePhotoUrl": "http://example.com/photo.jpg",
                  "country": "IN",
                  "currency": "INR",
                  "language": "EN",
                  "timezone": "IST"
                }
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse))

        val input = UpdateProfileInput(
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            country = "IN"
        )
        val result = dataSource.updateProfile(input)

        assertEquals(true, result)
        
        val recordedRequest = mockWebServer.takeRequest()
        val requestBody = recordedRequest.body.readUtf8()
        assertNotNull(requestBody.contains("mutation UpdateProfile"))
        assertNotNull(requestBody.contains("johndoe"))
    }

    @Test
    fun `getMe returns user profile`() = runBlocking {
        val jsonResponse = """
            {
              "data": {
                "me": {
                  "id": "1",
                  "email": "test@example.com",
                  "firstName": "John",
                  "lastName": "Doe",
                  "username": "johndoe",
                  "profilePhotoUrl": null,
                  "country": "IN"
                }
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse))

        val response = dataSource.getMe()

        assertNotNull(response)
        assertEquals("test@example.com", response?.email)
        assertEquals("johndoe", response?.username)
    }

    @Test
    fun `verifyEmailOtp returns true`() = runBlocking {
        val jsonResponse = """
            {
              "data": {
                "verifyEmailOtp": true
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse))

        val result = dataSource.verifyEmailOtp("test@example.com", "123456")

        assertEquals(true, result)
    }
}
