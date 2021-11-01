package com.example.myapplication.domain.usecase

import com.example.myapplication.data.remote.ProductApiService
import com.example.myapplication.domain.repository.ProductRepository

internal class GetProductDetailsUseCase(private val productRepository: ProductRepository) {
    operator fun invoke(id: Int) = productRepository.getProductById(id)
}