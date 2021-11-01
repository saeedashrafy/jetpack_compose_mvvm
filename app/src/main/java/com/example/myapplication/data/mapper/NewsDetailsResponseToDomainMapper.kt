package com.example.myapplication.data.mapper

import com.example.myapplication.core.Mapper
import com.example.myapplication.data.remote.model.NewsDetailsResponse
import com.example.myapplication.domain.entity.NewsDetails


class NewsDetailsResponseToDomainMapper : Mapper<NewsDetailsResponse, NewsDetails> {
    override fun invoke(response: NewsDetailsResponse): NewsDetails {
        return response.details.let {
            NewsDetails(it.title, it.description,it.content, url = it.url)
        }
    }

}