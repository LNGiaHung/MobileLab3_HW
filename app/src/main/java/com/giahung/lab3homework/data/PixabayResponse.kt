package com.giahung.lab3homework.data

import com.google.gson.annotations.SerializedName

data class PixabayResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<Image>
)

data class Image(
    val id: Int,
    @SerializedName("webformatURL") val webformatUrl: String,
    val tags: String,
    @SerializedName("previewURL") val previewUrl: String
)