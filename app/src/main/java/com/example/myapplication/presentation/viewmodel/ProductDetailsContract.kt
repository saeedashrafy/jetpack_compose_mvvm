package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.domain.entity.Failure
import com.example.myapplication.domain.entity.Product
import com.example.myapplication.domain.entity.ProductDetails

internal sealed class DetailsViewIntent {
    data class Initial(val id:Int) : DetailsViewIntent()
    object Refresh : DetailsViewIntent()
    object Retry : DetailsViewIntent()

}

internal data class DetailsViewState(
    val productDetails: ProductDetails?,
    val isLoading: Boolean,
    val error: Failure?,
    val isRefreshing: Boolean
) {
    companion object {
        fun initial() = DetailsViewState(
            productDetails = null,
            isLoading = true,
            error = null,
            isRefreshing = false
        )
    }
}


internal sealed class DetailsPartialChange {
    abstract fun reduce(vs: DetailsViewState): DetailsViewState
    sealed class GetProduct : DetailsPartialChange() {

        object Loading : GetProduct()
        data class Success(val productDetails: ProductDetails) : GetProduct()
        data class FailureData(val failure: Failure) : GetProduct()

        override fun reduce(vs: DetailsViewState): DetailsViewState {
            return when (this) {
                Loading -> vs.copy(
                    isLoading = true,
                    error = null
                )
                is Success -> vs.copy(
                    isLoading = false,
                    error = null,
                    productDetails = productDetails
                )
                is FailureData -> vs.copy(
                    isLoading = false,
                    error = failure
                )
            }
        }
    }

    sealed class Refresh : DetailsPartialChange() {
        override fun reduce(vs: DetailsViewState): DetailsViewState {
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


internal sealed class DetailsSingleEvent {
    sealed class Refresh : DetailsSingleEvent() {
        object Success : Refresh()
        data class FailureData(val error: Failure) : Refresh()
    }

    data class GetProductsError(val error: Failure) : DetailsSingleEvent()
}


