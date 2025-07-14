package com.memory.keeper.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RecordRequest(
    val conversation: String,
    val feedback: String? = null,
    val dailyDayRecording: String,
)
