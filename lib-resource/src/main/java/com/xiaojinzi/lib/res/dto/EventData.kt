package com.xiaojinzi.lib.res.dto

import androidx.annotation.Keep

enum class EventFrom {
    UI, Other
}

@Keep
data class EventData<T>(
    val from: EventFrom = EventFrom.Other,
    val data: T,
)