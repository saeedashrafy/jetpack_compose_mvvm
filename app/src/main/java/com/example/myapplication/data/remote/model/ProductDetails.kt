package com.example.myapplication.data.remote.model

import com.squareup.moshi.Json

data class ProductDetails(
    @Json(name = "id")
    val id: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "price")
    val buyPrice: String,
    @Json(name = "images")
    val image: List<Image>?,
    @Json(name = "compare_price")
    val sellPrice: String,
    @Json(name = "fields")
    val fields: List<Fields>?
)

data class Fields(
    @Json(name = "name")
    val name: String,
    @Json(name = "value")
    val value: String,
)

data class Image(
    @Json(name = "path")
    val path: String
)