package com.example.myapplication.data.mapper

import android.util.Log
import com.example.myapplication.core.Mapper
import com.example.myapplication.data.remote.model.NewsResponse
import com.example.myapplication.domain.entity.News
import com.example.myapplication.domain.entity.NewsList

internal class NewsResponseToNewsDomainMapper : Mapper<NewsResponse, NewsList> {
    override fun invoke(response: NewsResponse): NewsList {

        return response.news.map { item ->
            News(
                id = item.id,
                title = item.title,
                description = item.description,
                image = item.image,
                publishDate = item.publish?.date,
                url= item.url,
                subtractDate = item.publish?.subtract

            )
        }.toList().let {
            NewsList(it)
        }
    }
}