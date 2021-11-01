package com.example.myapplication.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.IntentDispatcher
import com.example.myapplication.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take

internal class ProductVM(private val getProductsUseCase: GetProductsUseCase) : ViewModel() {
    private val _eventChannel = Channel<SingleEvent>(Channel.BUFFERED)
    private val _intentFlow = MutableSharedFlow<ViewIntent>(extraBufferCapacity = 64)

    private val viewState: StateFlow<ViewState>


    @Composable
    internal operator fun component1(): ViewState = viewState.collectAsState().value
    internal operator fun component2(): Flow<SingleEvent> = _eventChannel.receiveAsFlow()
    internal operator fun component3(): IntentDispatcher<ViewIntent> = { _intentFlow.tryEmit(it) }


    init {
        val initialVS = ViewState.initial()
        viewState = merge(
            _intentFlow.filterIsInstance<ViewIntent.Initial>().take(1),
            _intentFlow.filterNot { it is ViewIntent.Initial }
        )
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
            .toPartialChangeFlow()
            .sendSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .catch { Log.d("###", "[MAIN_VM] Throwable: $it") }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVS
            )
    }

    private fun Flow<PartialChange>.sendSingleEvent(): Flow<PartialChange> {
        return onEach {
            val event = when (it) {
                is PartialChange.GetProducts.FailureData -> SingleEvent.GetProductsError(error = it.failure)
                is PartialChange.Refresh.FailureData -> SingleEvent.Refresh.FailureData(it.error)
                is PartialChange.Refresh.Success -> SingleEvent.Refresh.Success
                PartialChange.GetProducts.Loading -> return@onEach
                is PartialChange.GetProducts.Success -> return@onEach
                PartialChange.Refresh.Loading -> return@onEach
                is PartialChange.GetProducts.Loading -> return@onEach
            }
            _eventChannel.send(event)
        }
    }

    @OptIn(FlowPreview::class)
    private fun Flow<ViewIntent>.toPartialChangeFlow(): Flow<PartialChange> {
        val getProductChanges = getProductsUseCase()
            .map {
                it.fold({ failure ->
                    PartialChange.GetProducts.FailureData(failure) as PartialChange.GetProducts
                }, { products ->
                    PartialChange.GetProducts.Success(
                        products.productList
                    ) as PartialChange.GetProducts
                })
                    .let {
                        return@map it as PartialChange
                    }
            }
            .onStart { emit(PartialChange.GetProducts.Loading) }
        return merge(
            filterIsInstance<ViewIntent.Initial>()
                .flatMapConcat { getProductChanges },
        )
    }


    private fun <T : ViewIntent> Flow<T>.logIntent() = onEach { Log.d("MainVM", "## Intent: $it") }
}