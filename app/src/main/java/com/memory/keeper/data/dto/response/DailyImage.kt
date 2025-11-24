package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class DailyImage(
    val dailyImageId: Long,
    val url: String
)
