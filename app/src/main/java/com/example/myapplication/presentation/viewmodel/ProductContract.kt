package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.domain.entity.Failure
import com.example.myapplication.domain.entity.Product

internal sealed class ViewIntent {
    object Initial : ViewIntent()
    object Refresh : ViewIntent()
    object Retry : ViewIntent()

}

internal data class ViewState(
    val productItems: List<Product>,
    val isLoading: Boolean,
    val error: Failure?,
    val isRefreshing: Boolean
) {
    companion object {
        fun initial() = ViewState(
            productItems = emptyList(),
            isLoading = true,
            error = null,
            isRefreshing = false
        )
    }
}


internal sealed class PartialChange {
    abstract fun reduce(vs: ViewState): ViewState
    sealed class GetProducts : PartialChange() {

        object Loading : GetProducts()
        data class Success(val products: List<Product>) : GetProducts()
        data class FailureData(val failure: Failure) : GetProducts()

        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                Loading -> vs.copy(
                    isLoading = true,
                    error = null
                )
                is Success -> vs.copy(
                    isLoading = false,
                    error = null,
                    productItems = products
                )
                is FailureData -> vs.copy(
                    isLoading = false,
                    error = failure
                )
            }
        }
    }

    sealed class Refresh : PartialChange() {
        override fun reduce(vs: ViewState): ViewState {
            return when (this) {
                is Success -> vs.copy(isRefreshing = false)
                is FailureData -> vs.copy(isRefreshing = false)
                Loading -> vs.copy(isRefreshing = true)
            }
        }

        object Loading : Refresh()
        object Success : Refresh()
        data class FailureData(val error: Failure) : Refresh()
    }
}


internal sealed class SingleEvent {
    sealed class Refresh : SingleEvent() {
        object Success : Refresh()
        data class FailureData(val error: Failure) : Refresh()
    }

    data class GetProductsError(val error: Failure) : SingleEvent()


}