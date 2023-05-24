package com.code.cancer.hook.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStateAtLeast
import com.code.cancer.hook.HookApplication
import com.code.cancer.hook.data.HookInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

object EventBus {

    const val TAG = "EventBus"
    private val flowEvents = ConcurrentHashMap<String, MutableSharedFlow<Event>>()
    private val appScope = HookApplication.appScope

    fun getFlow(key: String): MutableSharedFlow<Event> {
        return flowEvents[key] ?: MutableSharedFlow<Event>(replay = 0).also {
            flowEvents[key] = it
        }
    }

    fun post(event: Event, delay: Long = 0) {
        appScope.launchDelay(delay = delay) {
            val simpleName = event.javaClass.simpleName
            getFlow(simpleName).emit(event)
        }
    }

    inline fun <reified T : Event> observe(
        lifecycleOwner: LifecycleOwner,
        minState: Lifecycle.State = Lifecycle.State.CREATED,
        crossinline onReceived: (T) -> Unit
    ) = getFlow(T::class.java.simpleName).collectIn(lifecycleOwner.lifecycleScope) {
        lifecycleOwner.lifecycle.whenStateAtLeast(minState) {
            if (it is T) {
                onReceived(it)
            }
        }
    }

}

sealed class Event {
    class OnHooked(val hookInfo: HookInfo): Event()
    object Clean: Event()
}