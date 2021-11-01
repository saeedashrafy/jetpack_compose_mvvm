package com.example.myapplication.domain.repository

import com.example.myapplication.domain.entity.DomainResult
import com.example.myapplication.domain.entity.News
import com.example.myapplication.domain.entity.NewsDetails
import com.example.myapplication.domain.entity.NewsList
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getAllNews(pageNumber: Int): Flow<DomainResult<NewsList>>

    fun getNewsDetails(id: Int): Flow<DomainResult<NewsDetails>>
}