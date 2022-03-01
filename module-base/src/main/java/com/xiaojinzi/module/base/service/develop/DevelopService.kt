package com.xiaojinzi.module.base.service.develop

import androidx.annotation.Keep
import com.xiaojinzi.support.annotation.HotObservable
import io.reactivex.Observable

@Keep
data class KeyAndDefValue(
    val key: String,
    val defBool: Boolean = DevelopService.DEFAULT_CHECK_BOX,
    val defInt: Int = DevelopService.DEFAULT_SINGLE_SELECT
)

interface DevelopService {

    companion object {

        // 单选的默认值
        const val DEFAULT_SINGLE_SELECT = -1
        const val DEFAULT_CHECK_BOX = false

        const val SP_FILE_NAME = "sp_develop"

        const val SP_KEY_IS_DEVELOP_VIEW_VISIBLE = "developViewVisible"

    }

    suspend fun dotDelay()

    /**
     * 保存一个值
     */
    fun saveValue(
        key: String,
        value: Int
    )

    /**
     * @param key 见常量
     */
    fun toggleValue(key: String)

    /**
     * 订阅某一个值
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    fun subscribeBehaviorIntValue(
        key: String,
        def: Int = getDefInt(key = key)
    ): Observable<Int>


    @HotObservable(HotObservable.Pattern.PUBLISH)
    fun subscribePublishBoolValue(key: String, ): Observable<Boolean>

    /**
     * 订阅某一个值
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    fun subscribeBehaviorBoolValue(
        key: String,
        def: Boolean = getDefBool(key = key)
    ): Observable<Boolean>

    fun getBoolValue(
        key: String
    ): Boolean {
        return getBoolValue(key = key, def = getDefBool(key))
    }

    fun getBoolValue(
        key: String,
        def: Boolean = getDefBool(key)
    ): Boolean

    fun getIntValue(
        key: String
    ): Int {
        return getIntValue(key = key, def = getDefInt(key = key))
    }

    fun getIntValue(
        key: String,
        def: Int = getDefInt(key = key)
    ): Int

    // 获取 key 默认的值

    fun getDefBool(key: String): Boolean
    fun getDefInt(key: String): Int

}