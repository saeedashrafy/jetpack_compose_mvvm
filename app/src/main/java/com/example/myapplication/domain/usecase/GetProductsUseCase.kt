package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.ProductRepository

class GetProductsUseCase(private val productRepository: ProductRepository) {
    operator fun invoke() = productRepository.getProducts()
}