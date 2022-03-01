package com.xiaojinzi.module.base.support.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface SharedStateFlow<T> : SharedFlow<T> {

    /**
     * 缓存的值
     */
    val value: T

    /**
     * 是否初始化了
     */
    val isInit: Boolean

}

interface MutableSharedStateFlow<T> : SharedStateFlow<T>, MutableSharedFlow<T> {

    /**
     * 缓存的值
     */
    override var value: T

    /**
     * 重放
     */
    fun replay()

}

private class MutableSharedStateFlowImpl<T>(
    val target: MutableSharedFlow<T>
) : MutableSharedStateFlow<T>, MutableSharedFlow<T> by target {

    override var value: T
        get() {
            synchronized(this) {
                if (target.replayCache.isEmpty()) {
                    error("you must set value first")
                }
                return target.replayCache.first()
            }
        }
        set(value) {
            synchronized(this) {
                target.tryEmit(value = value)
            }
        }

    override val isInit: Boolean
        get() = target.replayCache.isNotEmpty()

    override fun replay() {
        this.value = this.value
    }

}

@Suppress("FunctionName", "UNCHECKED_CAST")
fun <T> MutableSharedStateFlow(): MutableSharedStateFlow<T> {
    val shareFlow = MutableSharedFlow<T>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    return MutableSharedStateFlowImpl(
        target = shareFlow
    )
}

@Suppress("FunctionName", "UNCHECKED_CAST")
fun <T> MutableSharedStateFlow(initValue: T): MutableSharedStateFlow<T> {
    val shareFlow = MutableSharedFlow<T>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val result = shareFlow.tryEmit(value = initValue)
    if (!result) {
        error("MutableSharedStateFlow Fail to emit initValue")
    }
    return MutableSharedStateFlowImpl(
        target = shareFlow
    )
}

private fun <T> Flow<T>.doSharedStateIn(
    shareFlow: MutableSharedStateFlow<T>,
    scope: CoroutineScope,
    isTakeOne: Boolean = false,
    dropIfValueIsSet: Boolean = false,
) {
    val upstream = this
    scope.launch {
        if (isTakeOne) {
            val upstreamFirstValue = upstream.first()
            if (dropIfValueIsSet) {
                if (!shareFlow.isInit) {
                    shareFlow.value = upstreamFirstValue
                }
            } else {
                shareFlow.value = upstream.first()
            }
            this.cancel()
        } else {
            upstream.collect {
                if (dropIfValueIsSet) {
                    if (!shareFlow.isInit) {
                        shareFlow.value = it
                    }
                    this.cancel()
                } else {
                    shareFlow.value = it
                }
            }
        }
    }
}

fun <T> Flow<T>.sharedStateIn(
    scope: CoroutineScope,
    isTakeOne: Boolean = false,
    dropIfValueIsSet: Boolean = false,
): MutableSharedStateFlow<T> {
    val shareFlow = MutableSharedStateFlow<T>()
    this.doSharedStateIn(
        shareFlow = shareFlow,
        scope = scope,
        isTakeOne = isTakeOne,
        dropIfValueIsSet = dropIfValueIsSet,
    )
    return shareFlow
}

/**
 * 这里为什么要塞一个默认值. 是因为对于 Behavior 模式来说, 一定要有一个值. 即使这个值是 null
 * 为什么 RxJava 的 Behavior 模式可以不用给默认值, 是因为你去取 value 的时候默认是 null 呀
 * RxJava 不允许信号的值是 null, 所以这种特殊情况 RxJava 是可以区别出来的. 但是 Flow 是不可以的
 * 因为 Flow 的信号是可以为空的, 这也导致了默认 null 可能会和用户要表示的值的类型发生冲突. 所以这里一定要
 * 用户把值塞进来, 就算是一个 null
 */
fun <T> Flow<T>.sharedStateIn(
    initValue: T,
    scope: CoroutineScope,
    isTakeOne: Boolean = false,
    dropIfValueIsSet: Boolean = false,
): MutableSharedStateFlow<T> {
    val shareFlow = MutableSharedStateFlow(initValue = initValue)
    this.doSharedStateIn(
        shareFlow = shareFlow,
        scope = scope,
        isTakeOne = isTakeOne,
        dropIfValueIsSet = dropIfValueIsSet,
    )
    return shareFlow
}

// 针对 Boolean 的扩展
fun MutableSharedStateFlow<Boolean>.toggle() {
    this.value = !this.value
}
