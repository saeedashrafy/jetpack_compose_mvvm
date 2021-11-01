package com.example.myapplication.data.remote.model

import com.squareup.moshi.Json

data class ContactResponse(
    @Json(name = "description")
    val description: String
)