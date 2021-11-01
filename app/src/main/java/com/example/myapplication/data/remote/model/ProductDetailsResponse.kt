package com.example.myapplication.data.remote.model

import com.squareup.moshi.Json

internal data class ProductDetailsResponse(
    @Json(name = "product")
    val productDetails: ProductDetails
)