package com.android.trippoint.trip.collaboration.domain.repository

import com.android.trippoint.trip.collaboration.domain.model.ActivityLog
import com.android.trippoint.trip.collaboration.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface CollaborationRepository {
    // Chat / Discussion
    suspend fun getMessages(
        tripId: String, 
        limit: Int? = 50, 
        beforeCursor: String? = null
    ): Result<List<ChatMessage>>
    
    fun observeMessages(tripId: String): Flow<ChatMessage>
    
    suspend fun sendMessage(
        tripId: String, 
        content: String, 
        type: String = "TEXT", 
        replyToId: String? = null
    ): Result<ChatMessage>
    
    suspend fun deleteMessage(tripId: String, messageId: String): Result<Boolean>
    
    // Activity Feed
    suspend fun getActivityLogs(
        tripId: String, 
        limit: Int? = 50, 
        beforeCursor: String? = null
    ): Result<List<ActivityLog>>
    
    fun observeActivityFeed(tripId: String): Flow<ActivityLog>
}
