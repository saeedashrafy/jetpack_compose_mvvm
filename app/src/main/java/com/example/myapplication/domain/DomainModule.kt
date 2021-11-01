package com.example.myapplication.domain


import com.example.myapplication.domain.usecase.*
import com.example.myapplication.domain.usecase.GetProductDetailsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.dsl.module

@FlowPreview
@ExperimentalCoroutinesApi
val domainModule = module {

    factory { GetProductsUseCase(get()) }
    factory { GetAllNewsUseCase(get()) }
    factory { GetProductDetailsUseCase(get()) }
    factory { GetNewsDetailsUseCase(get()) }
    factory { SubmitContactUseCase(get()) }
}