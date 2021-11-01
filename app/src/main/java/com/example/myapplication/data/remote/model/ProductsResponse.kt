package com.example.myapplication.data.remote.model

import com.squareup.moshi.Json

internal data class ProductsResponse(
    @Json(name = "products")
    val list: List<Product>
)