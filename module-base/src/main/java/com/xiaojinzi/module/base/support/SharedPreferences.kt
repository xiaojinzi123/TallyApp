package com.xiaojinzi.module.base.support

import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.ktx.AppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface SpPersistenceConverter<T1, T2> {

    fun converte(value: T1): T2

    fun parse(value: T2): T1

}

inline fun <reified T> spPersistenceGetValue(
    key: String,
): T? {
    return (when (T::class) {
        Boolean::class -> {
            spService.getBool(
                key = key,
            )
        }
        String::class -> {
            spService.getString(
                key = key,
            )
        }
        Int::class -> {
            spService.getInt(
                key = key,
            )
        }
        Long::class -> {
            spService.getLong(
                key = key,
            )
        }
        else -> notSupportError()
    }) as T?
}

inline fun <reified T> spPersistenceSetValue(
    key: String,
    value: T?
) {
    when (T::class) {
        Boolean::class -> {
            spService.putBool(
                key = key,
                value = value as Boolean?,
            )
        }
        String::class -> {
            spService.putString(
                key = key,
                value = value as String?,
            )
        }
        Int::class -> {
            spService.putInt(
                key = key,
                value = value as Int?,
            )
        }
        Long::class -> {
            spService.putLong(
                key = key,
                value = value as Long?,
            )
        }
        else -> notSupportError()
    }
}

inline fun <reified T> MutableSharedStateFlow<T?>.spPersistence(
    scope: CoroutineScope = AppScope,
    key: String,
    def: T? = null,
): MutableSharedStateFlow<T?> {
    val targetObservable = this
    scope.launch {
        val targetValue = spPersistenceGetValue<T>(
            key = key,
        ) ?: def
        targetObservable.value = targetValue
        targetObservable
            .onEach {
                spPersistenceSetValue<T>(
                    key = key,
                    value = it
                )
            }
            .collect()
    }
    return this
}

inline fun <reified T> MutableSharedStateFlow<T>.spPersistenceNonNull(
    scope: CoroutineScope = AppScope,
    key: String,
    def: T,
): MutableSharedStateFlow<T> {
    val targetObservable = this
    scope.launch {
        val targetValue = spPersistenceGetValue<T>(
            key = key,
        ) ?: def
        targetObservable.value = targetValue
        targetObservable
            .onEach {
                spPersistenceSetValue<T>(
                    key = key,
                    value = it
                )
            }
            .collect()
    }
    return this
}

inline fun <reified T, reified V> MutableSharedStateFlow<T?>.spConverterPersistence(
    scope: CoroutineScope = AppScope,
    key: String,
    def: T? = null,
    spPersistenceConverter: SpPersistenceConverter<T, V>,
): MutableSharedStateFlow<T?> {
    val targetObservable = this
    scope.launch {
        val targetValue = spPersistenceGetValue<V>(
            key = key,
        )?.let {
            spPersistenceConverter.parse(value = it)
        } ?: def
        targetObservable.value = targetValue
        targetObservable
            .onEach {
                spPersistenceSetValue<V>(
                    key = key,
                    value = it?.run {
                        spPersistenceConverter.converte(value = it)
                    }
                )
            }
            .collect()
    }
    return this
}

inline fun <reified T, reified V> MutableSharedStateFlow<T>.spConverterPersistenceNonNull(
    scope: CoroutineScope = AppScope,
    key: String,
    def: T,
    spPersistenceConverter: SpPersistenceConverter<T, V>,
): MutableSharedStateFlow<T> {
    val targetObservable = this
    scope.launch {
        val targetValue = spPersistenceGetValue<V>(
            key = key,
        )?.let {
            spPersistenceConverter.parse(value = it)
        } ?: def
        targetObservable.value = targetValue
        targetObservable
            .onEach {
                spPersistenceSetValue<V>(
                    key = key,
                    value = spPersistenceConverter.converte(value = it)
                )
            }
            .collect()
    }
    return this
}