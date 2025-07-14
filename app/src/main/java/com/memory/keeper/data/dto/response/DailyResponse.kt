package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class DailyResponse(
    val conversation: String,
    val createdAt: String,
    val dailyDayRecording: String,
    val feedback: String? = null,
    val id: Long,
    val imageUrls: List<String>,
    val updatedAt: String,
    val videoUrls: List<String>
)