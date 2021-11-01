package com.example.myapplication.data.remote

import com.example.myapplication.data.remote.model.ProductDetailsResponse
import com.example.myapplication.data.remote.model.ProductsResponse
import com.example.myapplication.data.remote.model.ResultResponse
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

internal interface ProductApiService {

    //@Headers("Content-Type: application/json")

    @GET("store/products")
    suspend fun getProducts(): ResultResponse<ProductsResponse>

    @GET("store/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ResultResponse<ProductDetailsResponse>

    companion object {
        operator fun invoke(retrofit: Retrofit) = retrofit.create<ProductApiService>()
    }


}