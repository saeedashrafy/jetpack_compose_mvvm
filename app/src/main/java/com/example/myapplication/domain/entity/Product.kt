package com.example.myapplication.domain.entity

data class Product(
    val id: Int,
    val nameEn: String,
    val nameFa: String,
    val image: String?,
    val buyPrice: String,
    val sellPrice: String
)