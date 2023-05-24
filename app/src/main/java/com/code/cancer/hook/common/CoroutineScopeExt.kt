package com.code.cancer.hook.common

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.launchDelay(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    delay: Long = 0,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(context, start) {
        delay(delay)
        block()
    }
}

inline fun <T> Flow<T>.collectIn(
    coroutineScope: CoroutineScope,
    crossinline action: suspend (value: T) -> Unit
) = coroutineScope.launch {
    collect {
        action(it)
    }
}

fun <T> FlowCollector<T>.emitIn(coroutineScope: CoroutineScope, vararg values: T) = coroutineScope.launch {
    for (value in values) emit(value)
}