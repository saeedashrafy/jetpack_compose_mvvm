package com.example.myapplication.data.remote

import com.example.myapplication.data.remote.model.NewsDetailsResponse
import com.example.myapplication.data.remote.model.NewsResponse
import com.example.myapplication.data.remote.model.ProductsResponse
import com.example.myapplication.data.remote.model.ResultResponse
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface NewsApiService {

    @GET("blog/posts")
    suspend fun getNewsList(@Query("page") page: Int): ResultResponse<NewsResponse>

    @GET("blog/posts/{id}")
    suspend fun getNewsDetails(@Path("id") id:Int):ResultResponse<NewsDetailsResponse>

    companion object {
        operator fun invoke(retrofit: Retrofit) = retrofit.create<NewsApiService>()
    }
}