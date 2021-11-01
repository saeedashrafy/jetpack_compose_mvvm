package com.example.myapplication.domain.repository

import com.example.myapplication.domain.entity.DomainResult
import com.example.myapplication.domain.entity.Product
import com.example.myapplication.domain.entity.ProductDetails
import com.example.myapplication.domain.entity.ProductList
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getProducts(): Flow<DomainResult<ProductList>>

    fun getProductById(id: Int): Flow<DomainResult<ProductDetails>>
}