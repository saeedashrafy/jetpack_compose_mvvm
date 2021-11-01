package com.example.myapplication.data.remote.model

import com.squareup.moshi.Json

data class ContactBody(
    @Json(name="name")
    val name:String?,
    @Json(name="email")
    val email :String?,
    @Json(name="phone")
    val phone:String?,
    @Json(name="subject")
    val subject:String?,
    @Json(name="body")
    val body:String?,

)