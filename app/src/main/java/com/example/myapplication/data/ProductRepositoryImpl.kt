package com.example.myapplication.data

import com.example.myapplication.core.Mapper
import com.example.myapplication.core.dispatchers.CoroutineDispatchers
import com.example.myapplication.data.remote.ProductApiService
import com.example.myapplication.data.remote.model.ProductDetailsResponse
import com.example.myapplication.data.remote.model.ProductsResponse
import com.example.myapplication.data.remote.safeCall
import com.example.myapplication.domain.entity.DomainResult
import com.example.myapplication.domain.entity.ProductDetails
import com.example.myapplication.domain.entity.ProductList
import com.example.myapplication.domain.repository.ProductRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class ProductRepositoryImpl(
    private val apiService: ProductApiService,
    private val dispatchers: CoroutineDispatchers,
    private val responseToDomain: Mapper<ProductsResponse, ProductList>,
    private val detailsResponseToDomain: Mapper<ProductDetailsResponse, ProductDetails>,
) : ProductRepository {


    @FlowPreview
    override fun getProducts(): Flow<DomainResult<ProductList>> {
        return flow {
            safeCall(dispatcher = dispatchers, responseToDomain) {
                apiService.getProducts()
            }.let {
                emit(it)
            }
        }
    }

    override fun getProductById(id: Int): Flow<DomainResult<ProductDetails>> {
        return flow {
            safeCall(dispatcher = dispatchers, detailsResponseToDomain) {
                apiService.getProductById(id)
            }.let {
                emit(it)
            }
        }
    }


}