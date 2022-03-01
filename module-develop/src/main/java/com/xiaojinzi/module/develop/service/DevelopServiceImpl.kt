package com.xiaojinzi.module.develop.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.service.develop.DevelopService
import com.xiaojinzi.module.base.support.spService
import com.xiaojinzi.module.develop.bean.keyAndDefValues
import com.xiaojinzi.support.ktx.ignoreCheckResult
import com.xiaojinzi.support.ktx.observeOnMainThread
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.delay

@ServiceAnno(DevelopService::class)
class DevelopServiceImpl: DevelopService {

    private val toggleValuesPublishSubjectMap = mutableMapOf<String, PublishSubject<Boolean>>()
    private val toggleValuesMap = mutableMapOf<String, BehaviorSubject<Boolean>>()

    private val intValuesMap = mutableMapOf<String, BehaviorSubject<Int>>()

    private fun getTogglePublishSubject(key: String): PublishSubject<Boolean> {
        return toggleValuesPublishSubjectMap
            .apply {
                synchronized(toggleValuesMap) {
                    if (!this.containsKey(key)) {
                        this[key] = PublishSubject.create()
                        this[key]!!
                            .observeOnMainThread()
                            .subscribeBy {
                                spService.putBool(DevelopService.SP_FILE_NAME, key, it)
                                getToggleBehaviorSubject(key = key).onNext(it)
                            }
                            .ignoreCheckResult()
                    }
                }
            }
            .getValue(key)
    }

    private fun getToggleBehaviorSubject(
        key: String,
        def: Boolean = false
    ): BehaviorSubject<Boolean> {
        return toggleValuesMap
            .apply {
                synchronized(toggleValuesMap) {
                    if (!this.containsKey(key)) {
                        this[key] = BehaviorSubject.createDefault(spService.getBool(DevelopService.SP_FILE_NAME, key, def))
                    }
                }
            }
            .getValue(key)
    }

    private fun getIntBehaviorSubject(
        key: String,
        def: Int = DevelopService.DEFAULT_SINGLE_SELECT
    ): BehaviorSubject<Int> {
        return intValuesMap
            .apply {
                synchronized(intValuesMap) {
                    if (!this.containsKey(key)) {
                        this[key] = BehaviorSubject.createDefault(spService.getInt(DevelopService.SP_FILE_NAME, key, def))
                        this[key]!!
                            .distinctUntilChanged()
                            .observeOnMainThread()
                            .subscribeBy {
                                spService.putInt(DevelopService.SP_FILE_NAME, key, it)
                            }
                            .ignoreCheckResult()
                    }
                }
            }
            .getValue(key)
    }

    override fun saveValue(
        key: String,
        value: Int
    ) {
        getIntBehaviorSubject(key).onNext(value)
    }

    override fun toggleValue(key: String) {
        getTogglePublishSubject(key = key).onNext(
            getToggleBehaviorSubject(key = key).value!!.not()
        )
    }

    override fun subscribeBehaviorIntValue(
        key: String,
        def: Int
    ): Observable<Int> {
        return getIntBehaviorSubject(key, def)
    }

    override fun subscribePublishBoolValue(key: String): Observable<Boolean> {
        return getTogglePublishSubject(key = key)
    }

    override fun subscribeBehaviorBoolValue(
        key: String,
        def: Boolean
    ): Observable<Boolean> {
        return getToggleBehaviorSubject(key = key, def = def)
    }

    override fun getBoolValue(
        key: String,
        def: Boolean
    ): Boolean {
        return getToggleBehaviorSubject(key = key, def = def).value!!
    }

    override fun getIntValue(
        key: String,
        def: Int
    ): Int {
        return getIntBehaviorSubject(key = key, def).value!!
    }

    override fun getDefBool(key: String): Boolean {
        return keyAndDefValues
            .firstOrNull { it.key == key }
            ?.defBool ?: DevelopService.DEFAULT_CHECK_BOX
    }

    override fun getDefInt(key: String): Int {
        return keyAndDefValues
            .firstOrNull { it.key == key }
            ?.defInt ?: DevelopService.DEFAULT_SINGLE_SELECT
    }

    override suspend fun dotDelay() {
        delay(500)
    }

}