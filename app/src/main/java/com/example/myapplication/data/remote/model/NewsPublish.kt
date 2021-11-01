package com.example.myapplication.data.remote.model

import com.squareup.moshi.Json

data class NewsPublish(
    @Json(name = "year")
    val year: String,
    @Json(name = "month")
    val month: String,
    @Json(name = "date")
    val date: String,
    @Json(name="subtract")
    val subtract :String,
)