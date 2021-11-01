package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.domain.entity.Failure
import com.example.myapplication.domain.entity.News
import com.example.myapplication.domain.entity.Product


internal sealed class NewsViewIntent {
    object Initial : NewsViewIntent()
    object Refresh : NewsViewIntent()
    data class LoadMore(val index: Int) : NewsViewIntent()
    object Retry : NewsViewIntent()

}

internal data class NewsViewState(
    val news: List<News>,
    val isLoading: Boolean,
    val isLoadingMore: Boolean,
    val error: Failure?,
    val isRefreshing: Boolean
) {
    companion object {
        fun initial() = NewsViewState(
            news = emptyList(),
            isLoading = true,
            isLoadingMore = false,
            error = null,
            isRefreshing = false
        )
    }
}


internal sealed class NewsPartialChange {
    abstract fun reduce(vs: NewsViewState): NewsViewState
    sealed class GetNews : NewsPartialChange() {

        object Loading : GetNews()
        object LoadingMore : GetNews()
        data class Success(val news: List<News>) : GetNews()
        data class FailureData(val failure: Failure) : GetNews()

        override fun reduce(vs: NewsViewState): NewsViewState {
            return when (this) {
                Loading -> vs.copy(
                    isLoading = true,
                    error = null
                )
                LoadingMore -> vs.copy(
                    isLoading = false,
                    isLoadingMore = true,
                    error = null,
                )
                is Success -> vs.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = null,
                    news = vs.news.plus(news)
                )
                is FailureData -> vs.copy(
                    isLoading = false,
                    error = failure
                )
            }
        }
    }

    sealed class Refresh : NewsPartialChange() {
        override fun reduce(vs: NewsViewState): NewsViewState {
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


internal sealed class NewsSingleEvent {
    sealed class Refresh : NewsSingleEvent() {
        object Success : Refresh()
        data class FailureData(val error: Failure) : Refresh()
    }

    data class GetNewsError(val error: Failure) : NewsSingleEvent()


}