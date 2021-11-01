package com.example.myapplication.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.IntentDispatcher
import com.example.myapplication.domain.usecase.GetProductDetailsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

internal class ProductDetailsVM(private val getProductDetailsUseCase: GetProductDetailsUseCase) :
    ViewModel() {
    private val _eventChannel = Channel<DetailsSingleEvent>(Channel.BUFFERED)
    private val _intentFlow = MutableSharedFlow<DetailsViewIntent>(extraBufferCapacity = 64)

    private val detialsViewState: StateFlow<DetailsViewState>

    @Composable
    internal operator fun component1(): DetailsViewState = detialsViewState.collectAsState().value
    internal operator fun component2(): Flow<DetailsSingleEvent> = _eventChannel.receiveAsFlow()
    internal operator fun component3(): IntentDispatcher<DetailsViewIntent> =
        { _intentFlow.tryEmit(it) }

    init {
        val initialVS = DetailsViewState.initial()
        detialsViewState = merge(
            _intentFlow.filterIsInstance<DetailsViewIntent.Initial>().take(1),
            _intentFlow.filterNot { it is DetailsViewIntent.Initial }
        )
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
            .toDetailsPartialChangeFlow()
            .sendDetailsSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .catch { Log.d("###", "[MAIN_VM] Throwable: $it") }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVS
            )
    }

    private fun Flow<DetailsPartialChange>.sendDetailsSingleEvent(): Flow<DetailsPartialChange> {
        return onEach {
            val event = when (it) {
                is DetailsPartialChange.GetProduct.FailureData -> DetailsSingleEvent.GetProductsError(
                    error = it.failure
                )
                is DetailsPartialChange.Refresh.FailureData -> DetailsSingleEvent.Refresh.FailureData(
                    it.error
                )
                is DetailsPartialChange.Refresh.Success -> DetailsSingleEvent.Refresh.Success
                DetailsPartialChange.GetProduct.Loading -> return@onEach
                is DetailsPartialChange.GetProduct.Success -> return@onEach
                DetailsPartialChange.Refresh.Loading -> return@onEach
                is DetailsPartialChange.GetProduct.Loading -> return@onEach
            }
            _eventChannel.send(event)
        }
    }

    @OptIn(FlowPreview::class)
    private fun Flow<DetailsViewIntent>.toDetailsPartialChangeFlow(): Flow<DetailsPartialChange> {


        return merge(
            filterIsInstance<DetailsViewIntent.Initial>()
                .map { it.id }
                .flatMapConcat {
                    getProductDetailsUseCase(it)
                        .map {
                            it.fold({ failure ->
                                DetailsPartialChange.GetProduct.FailureData(failure) as DetailsPartialChange.GetProduct
                            }, { detils ->
                                DetailsPartialChange.GetProduct.Success(detils) as DetailsPartialChange.GetProduct
                            })
                                .let {
                                    return@map it as DetailsPartialChange
                                }
                        }
                        .onStart { emit(DetailsPartialChange.GetProduct.Loading) }
                },
        )
    }
}