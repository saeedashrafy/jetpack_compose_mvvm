package com.example.myapplication.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.IntentDispatcher
import com.example.myapplication.domain.usecase.GetNewsDetailsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

internal class NewsDetailsVM(private val getNewsDetailsUseCase: GetNewsDetailsUseCase) :
    ViewModel() {
    private val _eventChannel = Channel<NewsDetailsSingleEvent>(Channel.BUFFERED)
    private val _intentFlow = MutableSharedFlow<NewsDetailsViewIntent>(extraBufferCapacity = 64)

    private val detailsViewState: StateFlow<NewsDetailsViewState>

    @Composable
    internal operator fun component1(): NewsDetailsViewState =
        detailsViewState.collectAsState().value

    internal operator fun component2(): Flow<NewsDetailsSingleEvent> = _eventChannel.receiveAsFlow()
    internal operator fun component3(): IntentDispatcher<NewsDetailsViewIntent> =
        { _intentFlow.tryEmit(it) }

    init {
        val initialVS = NewsDetailsViewState.initial()
        detailsViewState = merge(
            _intentFlow.filterIsInstance<NewsDetailsViewIntent.Initial>().take(1),
            _intentFlow.filterNot { it is NewsDetailsViewIntent.Initial }
        )
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
            .toNewsDetailsPartialChangeFlow()
            .sendDetailsSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .catch { Log.d("###", "[MAIN_VM] Throwable: $it") }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVS
            )
    }

    private fun Flow<NewsDetailsPartialChange>.sendDetailsSingleEvent(): Flow<NewsDetailsPartialChange> {
        return onEach {
            val event = when (it) {
                is NewsDetailsPartialChange.GetDetails.FailureData -> NewsDetailsSingleEvent.GetDetailsError(
                    error = it.failure
                )
                is NewsDetailsPartialChange.Refresh.FailureData -> NewsDetailsSingleEvent.Refresh.FailureData(
                    it.error
                )
                is NewsDetailsPartialChange.Refresh.Success -> NewsDetailsSingleEvent.Refresh.Success
                NewsDetailsPartialChange.GetDetails.Loading -> return@onEach
                is NewsDetailsPartialChange.GetDetails.Success -> return@onEach
                NewsDetailsPartialChange.Refresh.Loading -> return@onEach
                is NewsDetailsPartialChange.GetDetails.Loading -> return@onEach
            }
            _eventChannel.send(event)
        }
    }

    @OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun Flow<NewsDetailsViewIntent>.toNewsDetailsPartialChangeFlow(): Flow<NewsDetailsPartialChange> {


        return merge(
            filterIsInstance<NewsDetailsViewIntent.Initial>()
                .map { it.id }
                .flatMapConcat {
                    getNewsDetailsUseCase(it)
                        .map {
                            it.fold({ failure ->
                                NewsDetailsPartialChange.GetDetails.FailureData(failure) as NewsDetailsPartialChange.GetDetails
                            }, { detils ->
                                NewsDetailsPartialChange.GetDetails.Success(detils) as NewsDetailsPartialChange.GetDetails
                            })
                                .let {
                                    return@map it as NewsDetailsPartialChange
                                }
                        }
                        .onStart { emit(NewsDetailsPartialChange.GetDetails.Loading) }
                },
        )
    }
}
