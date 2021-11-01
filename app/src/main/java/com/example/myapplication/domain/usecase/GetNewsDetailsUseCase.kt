package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.NewsRepository

class GetNewsDetailsUseCase(private val repository: NewsRepository) {
    operator fun invoke(id: Int) = repository.getNewsDetails(id)
}