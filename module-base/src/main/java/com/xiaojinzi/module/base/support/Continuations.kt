package com.xiaojinzi.module.base.support

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

public inline fun <T> Continuation<T>.tryResume(value: T): Unit = try {
    this.resume(value = value)
} catch (e: Exception) {
    // ignore
}
