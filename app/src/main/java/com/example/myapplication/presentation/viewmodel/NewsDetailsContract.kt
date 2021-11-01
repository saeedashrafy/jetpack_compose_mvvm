package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.domain.entity.Failure
import com.example.myapplication.domain.entity.NewsDetails

internal sealed interface NewsDetailsViewIntent {
    data class Initial(val id: Int) : NewsDetailsViewIntent
    object Refresh : NewsDetailsViewIntent
    object Retry : NewsDetailsViewIntent

}

internal data class NewsDetailsViewState(
    val details: NewsDetails?,
    val isLoading: Boolean,
    val error: Failure?,
    val isRefreshing: Boolean
) {
    companion object {
        fun initial() = NewsDetailsViewState(
            details = null,
            isLoading = true,
            error = null,
            isRefreshing = false
        )
    }
}


internal sealed class NewsDetailsPartialChange {
    abstract fun reduce(vs: NewsDetailsViewState): NewsDetailsViewState
    sealed class GetDetails : NewsDetailsPartialChange() {

        object Loading : GetDetails()
        data class Success(val details: NewsDetails) : GetDetails()
        data class FailureData(val failure: Failure) : GetDetails()

        override fun reduce(vs: NewsDetailsViewState): NewsDetailsViewState {
            return when (this) {
                Loading -> vs.copy(
                    isLoading = true,
                    error = null
                )
                is Success -> vs.copy(
                    isLoading = false,
                    error = null,
                    details = details
                )
                is FailureData -> vs.copy(
                    isLoading = false,
                    error = failure
                )
            }
        }
    }

    sealed class Refresh : NewsDetailsPartialChange() {
        override fun reduce(vs: NewsDetailsViewState): NewsDetailsViewState {
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


internal sealed interface NewsDetailsSingleEvent {
    sealed interface Refresh : NewsDetailsSingleEvent {
        object Success : Refresh
        data class FailureData(val error: Failure) : Refresh
    }

    data class GetDetailsError(val error: Failure) : NewsDetailsSingleEvent

}
