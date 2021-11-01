package com.example.myapplication.domain.entity

import com.squareup.moshi.Json

data class NewsDetails(
    val title: String,
    val description: String,
    val content: String,
    val url: String
)