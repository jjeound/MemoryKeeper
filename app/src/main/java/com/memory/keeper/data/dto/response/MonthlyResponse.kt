package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class MonthlyResponse(
    val id: Long? = null,
    val monthlyDayRecording: String,
    val imageUrl: String? = null,
)