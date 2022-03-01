package com.xiaojinzi.module.base.support

import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.ktx.AppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

suspend inline fun <reified T> dbPersistenceGetValue(
    key: String,
): T? {
    return (when (T::class) {
        Boolean::class -> {
            dbCommonService.getBoolean(
                key = key,
            )
        }
        String::class -> {
            dbCommonService.getString(
                key = key,
            )
        }
        Int::class -> {
            dbCommonService.getInt(
                key = key,
            )
        }
        else -> notSupportError()
    }) as T?
}

suspend inline fun <reified T> dbPersistenceSetValue(
    key: String,
    value: T?
) {
    when (T::class) {
        Boolean::class -> {
            dbCommonService.saveBoolean(
                key = key,
                value = value as Boolean?,
            )
        }
        String::class -> {
            dbCommonService.saveString(
                key = key,
                value = value as String?,
            )
        }
        Int::class -> {
            dbCommonService.saveInt(
                key = key,
                value = value as Int?,
            )
        }
        else -> notSupportError()
    }
}

inline fun <reified T> MutableSharedStateFlow<T?>.dbPersistence(
    scope: CoroutineScope = AppScope,
    key: String,
    def: T? = null,
): MutableSharedStateFlow<T?> {
    val targetObservable = this
    scope.launch {
        val targetValue = dbPersistenceGetValue<T>(
            key = key,
        ) ?: def
        targetObservable.value = targetValue
        targetObservable
            .onEach {
                dbPersistenceSetValue(
                    key = key,
                    value = it
                )
            }
            .collect()
    }
    return this
}

inline fun <reified T> MutableSharedStateFlow<T>.dbPersistenceNonNull(
    scope: CoroutineScope = AppScope,
    key: String,
    def: T,
): MutableSharedStateFlow<T> {
    val targetObservable = this
    scope.launch {
        targetObservable.value = dbPersistenceGetValue<T>(
            key = key,
        )?: def
        targetObservable
            .onEach {
                dbPersistenceSetValue(
                    key = key,
                    value = it
                )
            }
            .collect()
    }
    return this
}

/*
fun MutableSharedStateFlow<String?>.dbPersistence(
    key: String,
    def: String? = null
): MutableSharedStateFlow<String?> {
    val targetObservable = this
    AppScope.launch {
        targetObservable.value = dbCommonService.getString(
            key = key,
        ) ?: def
        targetObservable
            .onEach {
                dbCommonService.saveString(
                    key = key,
                    value = it,
                )
            }
            .collect()
    }
    return this
}

fun MutableSharedStateFlow<Boolean>.dbPersistence(
    key: String,
    def: Boolean = false
): MutableSharedStateFlow<Boolean> {
    val targetObservable = this
    AppScope.launch {
        targetObservable.value = dbCommonService.getBoolean(
            key = key,
        ) ?: def
        targetObservable
            .onEach {
                dbCommonService.saveBoolean(
                    key = key,
                    value = it,
                )
            }
            .collect()
    }
    return this
}

fun MutableSharedStateFlow<Int>.dbPersistence(key: String, def: Int): MutableSharedStateFlow<Int> {
    val targetObservable = this
    AppScope.launch {
        targetObservable.value = dbCommonService.getInt(
            key = key,
        ) ?: def
        targetObservable
            .onEach {
                dbCommonService.saveInt(
                    key = key,
                    value = it,
                )
            }
            .collect()
    }
    return this
}*/
