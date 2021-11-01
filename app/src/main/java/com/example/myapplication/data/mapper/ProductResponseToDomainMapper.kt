package com.example.myapplication.data.mapper

import com.example.myapplication.core.Mapper
import com.example.myapplication.data.remote.model.ProductsResponse
import com.example.myapplication.domain.entity.ProductList

internal class ProductResponseToDomainMapper : Mapper<ProductsResponse, ProductList> {
    override fun invoke(response: ProductsResponse): ProductList {
        return response.list.map { item ->
            com.example.myapplication.domain.entity.Product(
                id = item.id,
                nameEn =item.title.split("/").last(),
                nameFa = item.title.split("/").first(),
                image = "https://www.your_base_url.com${item.image}",
                buyPrice = item.buyPrice.substring(0,item.buyPrice.indexOf(".") +2),
                sellPrice =item.sellPrice.substring(0,item.sellPrice.indexOf(".") +2),
            )
        }
            .toList().let { list ->
                ProductList(list)
            }
    }

}