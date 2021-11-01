package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.core.Mapper
import com.example.myapplication.core.dispatchers.CoroutineDispatchers
import com.example.myapplication.data.remote.NewsApiService
import com.example.myapplication.data.remote.model.NewsDetailsResponse
import com.example.myapplication.data.remote.model.NewsResponse
import com.example.myapplication.data.remote.model.ProductsResponse
import com.example.myapplication.data.remote.safeCall
import com.example.myapplication.domain.entity.*
import com.example.myapplication.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class NewsRepositoryImpl(
    private val apiService: NewsApiService,
    private val dispatchers: CoroutineDispatchers,
    private val responseToDomain: Mapper<NewsResponse, NewsList>,
    private val detailsResponseToDomain: Mapper<NewsDetailsResponse, NewsDetails>,
) : NewsRepository {
    override fun getAllNews(pageNumber: Int): Flow<DomainResult<NewsList>> {
        return flow {
            safeCall(dispatcher = dispatchers, responseToDomain) {
                apiService.getNewsList(pageNumber)
            }.let {
                emit(it)
            }
        }
    }

    override fun getNewsDetails(id: Int): Flow<DomainResult<NewsDetails>> {
        return flow {
            safeCall(dispatcher = dispatchers, detailsResponseToDomain) {
                apiService.getNewsDetails(id)
            }.let {
                emit(it)
            }
        }
    }
}