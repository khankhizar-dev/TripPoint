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

class CollaborationRemoteDataSourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var dataSource: CollaborationRemoteDataSource

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TripPointApi::class.java)
        dataSource = CollaborationRemoteDataSource(api)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getActivityLogs returns list of logs`() = runBlocking {
        val jsonResponse = """
            {
              "data": {
                "activityLogs": [
                  {
                    "id": "1",
                    "tripId": "trip1",
                    "userId": "user1",
                    "userName": "Rohan",
                    "userPhotoUrl": null,
                    "action": "updated task",
                    "targetType": "TASK",
                    "targetName": "Book flights",
                    "timestamp": "2026-09-13T10:30:00Z"
                  }
                ]
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse))

        val result = dataSource.getActivityLogs("trip1", 50, null)

        assertEquals(1, result.size)
        assertEquals("Rohan", result[0].userName)
        assertEquals("TASK", result[0].targetType)
    }

    @Test
    fun `getMessages returns list of messages`() = runBlocking {
        val jsonResponse = """
            {
              "data": {
                "messages": [
                  {
                    "id": "1",
                    "tripId": "trip1",
                    "senderId": "user1",
                    "senderName": "Rohan",
                    "senderPhotoUrl": null,
                    "content": "Hello",
                    "type": "TEXT",
                    "attachments": [],
                    "replyToId": null,
                    "replyToContent": null,
                    "timestamp": "10:30 AM",
                    "isMe": true,
                    "deleted": false
                  }
                ]
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse))

        val result = dataSource.getMessages("trip1", MessagePaginationInput())

        assertEquals(1, result.size)
        assertEquals("Hello", result[0].content)
        assertEquals(true, result[0].isMe)
    }

    @Test
    fun `sendMessage returns sent message`() = runBlocking {
        val jsonResponse = """
            {
              "data": {
                "sendMessage": {
                  "id": "2",
                  "tripId": "trip1",
                  "senderId": "user1",
                  "senderName": "Rohan",
                  "senderPhotoUrl": null,
                  "content": "World",
                  "type": "TEXT",
                  "attachments": null,
                  "replyToId": null,
                  "replyToContent": null,
                  "timestamp": "10:31 AM",
                  "isMe": true,
                  "deleted": false
                }
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse))

        val result = dataSource.sendMessage("trip1", SendMessageInput("World"))

        assertNotNull(result)
        assertEquals("World", result?.content)
    }

    @Test
    fun `deleteMessage returns true`() = runBlocking {
        val jsonResponse = """
            {
              "data": {
                "deleteMessage": true
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse))

        val result = dataSource.deleteMessage("trip1", "msg1")

        assertEquals(true, result)
    }
}
