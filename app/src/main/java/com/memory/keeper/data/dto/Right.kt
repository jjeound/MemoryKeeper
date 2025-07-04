package com.memory.keeper.data.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Right(
    @SerializedName("image_urls")
    val imageUrls: List<String>,
    @SerializedName("keywords")
    val keywords: List<Keyword>,
    @SerializedName("press_list")
    val pressList: List<String>,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("originalSource")
    val originalSource: List<OriginalSource>,
)