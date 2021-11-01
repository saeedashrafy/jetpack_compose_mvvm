package com.example.myapplication.data.remote

import com.example.myapplication.data.remote.model.ContactBody
import com.example.myapplication.data.remote.model.ContactResponse
import com.example.myapplication.data.remote.model.ResultResponse
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ContactApiService {

    @Headers("Content-Type: application/json")
    @POST("submissions/contact")
    suspend fun submitContact(@Body contactBody: ContactBody): ResultResponse<ContactResponse>

    companion object {
        operator fun invoke(retrofit: Retrofit) = retrofit.create<ContactApiService>()
    }
}