package com.example.myapplication.data.remote.model

import com.squareup.moshi.Json

internal data class Product(
    @Json(name = "id")
    val id: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "image")
    val image: String?,
    @Json(name = "price")
    val buyPrice: String,
    @Json(name = "compare_price")
    val sellPrice: String,
)