package com.example.myapplication.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.IntentDispatcher
import com.example.myapplication.core.flatMapFirst
import com.example.myapplication.domain.usecase.GetAllNewsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

internal class NewsVM(private val newsUseCase: GetAllNewsUseCase) : ViewModel() {
    private val _eventChannel = Channel<NewsSingleEvent>(Channel.BUFFERED)
    private val _intentFlow = MutableSharedFlow<NewsViewIntent>(extraBufferCapacity = 64)

    private val newsViewState: StateFlow<NewsViewState>


    @Composable
    internal operator fun component1(): NewsViewState = newsViewState.collectAsState().value
    internal operator fun component2(): Flow<NewsSingleEvent> = _eventChannel.receiveAsFlow()
    internal operator fun component3(): IntentDispatcher<NewsViewIntent> =
        { _intentFlow.tryEmit(it) }



    init {
        val initialVS = NewsViewState.initial()
        newsViewState = merge(
            _intentFlow.filterIsInstance<NewsViewIntent.Initial>().take(1),
            _intentFlow.filterNot { it is NewsViewIntent.Initial }
        )
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
            .onEach { Log.e("TAG",it.toString()) }
            .toPartialChangeFlow()
            .onEach { Log.e("TAG",it.toString()) }
            .sendNewsSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .catch { Log.d("###", "[MAIN_VM] Throwable: $it") }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVS
            )
    }

    private fun Flow<NewsPartialChange>.sendNewsSingleEvent(): Flow<NewsPartialChange> {
        return onEach {
            val event = when (it) {
                is NewsPartialChange.GetNews.FailureData -> NewsSingleEvent.GetNewsError(error = it.failure)
                is NewsPartialChange.Refresh.FailureData -> NewsSingleEvent.Refresh.FailureData(it.error)
                is NewsPartialChange.Refresh.Success -> NewsSingleEvent.Refresh.Success
                NewsPartialChange.GetNews.Loading -> return@onEach
                NewsPartialChange.GetNews.LoadingMore -> return@onEach
                is NewsPartialChange.GetNews.Success -> return@onEach
                NewsPartialChange.Refresh.Loading -> return@onEach
                is NewsPartialChange.GetNews.Loading -> return@onEach
            }
            _eventChannel.send(event)
        }
    }

    @OptIn(FlowPreview::class)
    private fun Flow<NewsViewIntent>.toPartialChangeFlow(): Flow<NewsPartialChange> {
        val getNewsChanges = newsUseCase()
            .map {
                it.fold({ failure ->
                    NewsPartialChange.GetNews.FailureData(failure) as NewsPartialChange.GetNews
                }, { newsList ->
                    NewsPartialChange.GetNews.Success(
                        newsList.news
                    ) as NewsPartialChange.GetNews
                })
                    .let {
                        return@map it as NewsPartialChange
                    }
            }
            .onStart { emit(NewsPartialChange.GetNews.Loading) }


        return merge(
            filterIsInstance<NewsViewIntent.Initial>()
                .flatMapConcat { getNewsChanges },
            filterIsInstance<NewsViewIntent.LoadMore>().map { it.index }.flatMapFirst { index ->
                newsUseCase((index / 10) + 1)
                    .map {
                        it.fold({ failure ->
                            NewsPartialChange.GetNews.FailureData(failure) as NewsPartialChange.GetNews
                        }, { newsList ->
                            NewsPartialChange.GetNews.Success(
                                newsList.news
                            ) as NewsPartialChange.GetNews
                        })
                            .let {
                                return@map it as NewsPartialChange
                            }
                    }
                    .onStart { emit(NewsPartialChange.GetNews.LoadingMore) }

            }
        )
    }


}