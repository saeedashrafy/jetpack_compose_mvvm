package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.NewsRepository

class GetAllNewsUseCase(private val newsRepository: NewsRepository) {
    operator fun invoke( pageNumber:Int = 1) = newsRepository.getAllNews(pageNumber)
}