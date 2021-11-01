package com.example.myapplication.data.remote.model

import com.squareup.moshi.Json

data class NewsDetails(
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "content")
    val content: String,
    @Json(name="url")
    val url:String
)