package com.example.myapplication.data.mapper

import com.example.myapplication.core.Mapper
import com.example.myapplication.data.remote.model.ProductDetailsResponse
import com.example.myapplication.domain.entity.Field
import com.example.myapplication.domain.entity.ProductDetails

internal class ProductDetailsResponseToDomainMapper :
    Mapper<ProductDetailsResponse, ProductDetails> {
    override fun invoke(response: ProductDetailsResponse): ProductDetails {

        return ProductDetails(
            id = response.productDetails.id,
            nameEn = response.productDetails.title.split("/").last(),
            nameFa = response.productDetails.title.split("/").first(),
            sellPrice =response.productDetails.sellPrice.substring(0,(response.productDetails.sellPrice.indexOf(".")+ 2)),
            buyPrice = response.productDetails.buyPrice.substring(0,(response.productDetails.buyPrice.indexOf(".")+ 2)),
            image = "https://www.your_base_url.com${response.productDetails.image?.get(0)?.path}",
            fields = response.productDetails.fields?.map {
                Field(
                    name = it.name,
                    value = it.value.removePrefix("-"),
                    isDsc = it.value.startsWith("-")
                )
            }
        )
    }
}