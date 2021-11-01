package com.example.myapplication.data.remote.model

import com.squareup.moshi.Json

data class NewsDetailsResponse(
    @Json(name = "post")
    val details: NewsDetails
)