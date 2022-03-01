package com.xiaojinzi.module.base.support

import java.util.concurrent.atomic.AtomicLong

private val counter: AtomicLong = AtomicLong()

object Counter {

    fun get(): Long {
        return counter.incrementAndGet()
    }

}