package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class DailyResponse(
    val conversations: List<String>,
    val createdAt: String,
    val dailyDayRecording: String,
    val feedback: String? = null,
    val id: Long,
    val userId: Long,
    val imageUrls: List<String>? = null,
    val updatedAt: String,
    val videoUrl: String? = null
)