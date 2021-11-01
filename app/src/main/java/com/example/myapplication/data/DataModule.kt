package com.example.myapplication.data

import com.example.myapplication.BuildConfig
import com.example.myapplication.data.mapper.*
import com.example.myapplication.data.mapper.NewsResponseToNewsDomainMapper
import com.example.myapplication.data.mapper.ProductDetailsResponseToDomainMapper
import com.example.myapplication.data.mapper.ProductResponseToDomainMapper
import com.example.myapplication.data.remote.ContactApiService
import com.example.myapplication.data.remote.MyCallAdapterFactory
import com.example.myapplication.data.remote.NewsApiService
import com.example.myapplication.data.remote.ProductApiService
import com.example.myapplication.domain.repository.ContactRepository
import com.example.myapplication.domain.repository.NewsRepository
import com.example.myapplication.domain.repository.ProductRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.qualifier.named
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime

private const val BASE_URL = "BASE_URL"

@ExperimentalTime
@ExperimentalCoroutinesApi
val dataModule = module {

    factory { ProductResponseToDomainMapper() }
    factory { ProductDetailsResponseToDomainMapper() }
    factory { NewsResponseToNewsDomainMapper() }
    factory { NewsDetailsResponseToDomainMapper() }
    factory { ContactDomainToBodyMapper() }
    factory { ContactResponseToDomainMapper() }

    single<ContactRepository> {
        ContactRepositoryImpl(
            apiService = get(),
            dispatchers = get(),
            responseToDomainMapper = get<ContactResponseToDomainMapper>(),
            domainToBodyMapper = get<ContactDomainToBodyMapper>()
        )
    }

    single<ProductRepository> {
        ProductRepositoryImpl(
            apiService = get(),
            dispatchers = get(),
            responseToDomain = get<ProductResponseToDomainMapper>(),
            detailsResponseToDomain = get<ProductDetailsResponseToDomainMapper>(),
        )
    }
    single<NewsRepository> {
        NewsRepositoryImpl(
            apiService = get(),
            dispatchers = get(),
            responseToDomain = get<NewsResponseToNewsDomainMapper>(),
            detailsResponseToDomain = get<NewsDetailsResponseToDomainMapper>(),
        )
    }

    single { ProductApiService(retrofit = get()) }
    single { NewsApiService(retrofit = get()) }
    single { ContactApiService(retrofit = get()) }
    single {
        provideRetrofit(
            baseUrl = get(named(BASE_URL)),
            moshi = get(),
            client = get()
        )
    }

    single { provideMoshi() }

    single { provideOkHttpClient() }

    factory(named(BASE_URL)) { "https://www.your_base_url.com/site/api/v1/" }
}


private fun provideMoshi(): Moshi {
    return Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}

private fun provideRetrofit(baseUrl: String, moshi: Moshi, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .client(client)
        .addCallAdapterFactory(MyCallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(baseUrl)
        .build()
}

private fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply { level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE }
        )
        .build()
}