package com.example.myapplication.core

import com.example.myapplication.core.dispatchers.CoroutineDispatchers
import org.koin.dsl.module

val coreModule = module {
    single<CoroutineDispatchers> { CoroutineDispatchersImpl() }
}