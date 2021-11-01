package com.example.myapplication.data.remote.model

import com.squareup.moshi.Json

internal data class NewsResponse(
    @Json(name = "posts")
    val news: List<News>
)