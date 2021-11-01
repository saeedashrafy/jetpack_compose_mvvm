package com.example.myapplication.core

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference


@Suppress("unused")
inline val Any?.unit get() = Unit

fun <T, R> Flow<T>.flatMapFirst(transform: suspend (value: T) -> Flow<R>): Flow<R> =
    map(transform).flattenFirst()

fun <T> Flow<Flow<T>>.flattenFirst(): Flow<T> = channelFlow {
    val outerScope = this
    val busy = AtomicBoolean(false)
    collect { inner ->
        if (busy.compareAndSet(false, true)) {
            launch {
                try {
                    inner.collect { outerScope.send(it) }
                    busy.set(false)
                } catch (e: CancellationException) {
                    // cancel outer scope on cancellation exception, too
                    outerScope.cancel(e)
                }
            }
        }
    }
}


private object UNINITIALIZED

fun <A, B, R> Flow<A>.withLatestFrom(other: Flow<B>, transform: suspend (A, B) -> R): Flow<R> {
    return flow {
        coroutineScope {
            val latestB = AtomicReference<Any>(UNINITIALIZED)
            val outerScope = this

            launch {
                try {
                    other.collect { latestB.set(it) }
                } catch (e: CancellationException) {
                    outerScope.cancel(e) // cancel outer scope on cancellation exception, too
                }
            }

            collect { a ->
                val b = latestB.get()
                if (b != UNINITIALIZED) {
                    @Suppress("UNCHECKED_CAST")
                    emit(transform(a, b as B))
                }
            }
        }
    }
}