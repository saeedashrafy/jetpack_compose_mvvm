package com.example.myapplication.data.remote.model

import com.squareup.moshi.Json

data class News(
    @Json(name = "id")
    val id: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "image")
    val image: String?,
    @Json(name = "url")
    val url: String?,
    @Json(name = "published")
    val publish: NewsPublish?,
)