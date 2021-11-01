package com.example.myapplication.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.myapplication.presentation.viewmodel.*
import com.example.myapplication.presentation.viewmodel.NewsDetailsVM
import com.example.myapplication.presentation.viewmodel.NewsVM
import com.example.myapplication.presentation.viewmodel.ProductDetailsVM
import com.example.myapplication.presentation.viewmodel.ProductVM
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
@FlowPreview
val presentationModule = module {
    viewModel {
        ProductVM(
            get()
        )
    }
    viewModel {
        NewsVM(
            get()
        )
    }

    viewModel {
        ProductDetailsVM(get())
    }
    viewModel { NewsDetailsVM(get()) }
    viewModel { ContactVM(get(),SavedStateHandle()) }
}