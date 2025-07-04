package com.memory.keeper.data.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class NewsInfo(
    @SerializedName("image_urls")
    val imageUrls: List<String>,
    @SerializedName("representative_image")
    val representativeImage: String,
    @SerializedName("bias_ratio")
    val biasRatio: BiasRatio,
    @SerializedName("center")
    val center: Center?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("left")
    val left: Left?,
    @SerializedName("media_counts")
    val mediaCounts: MediaCounts,
    @SerializedName("model_ver")
    val modelVer: String,
    @SerializedName("pub_date")
    val pubDate: String,
    @SerializedName("right")
    val right: Right?,
    @SerializedName("title")
    val title: String,
    @SerializedName("updated_at")
    val updatedAt: String,
)