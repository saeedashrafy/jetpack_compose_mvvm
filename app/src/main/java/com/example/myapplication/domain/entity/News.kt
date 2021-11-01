package com.example.myapplication.domain.entity

data class News(
    val id: Int,
    val title: String,
    val description: String?,
    val image: String?,
    val url:String?,
    val publishDate: String?,
    val subtractDate: String?,
)