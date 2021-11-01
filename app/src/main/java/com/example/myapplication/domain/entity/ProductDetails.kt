package com.example.myapplication.domain.entity

import com.squareup.moshi.Json

data class ProductDetails(
    val id: Int,
    val nameEn: String,
    val nameFa: String,
    val buyPrice: String,
    val sellPrice: String,
    val image: String?,
    val fields: List<Field>?
)


data class Field(
    val name: String,
    val value: String,
    val isDsc :Boolean,
)