package com.memory.keeper.data.dto.request

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class ImageRequest(
    val dailyId: Long,
    val imageUrls: List<String>,
    val date: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
)
