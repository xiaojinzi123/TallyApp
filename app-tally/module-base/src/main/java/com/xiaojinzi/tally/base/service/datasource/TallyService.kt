package com.xiaojinzi.tally.base.service.datasource

import com.xiaojinzi.module.base.service.ImageUploadServerType
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.base.support.tallyAppService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 表示这个值是真实的值. 是一个 Long 类型
 * 并且相对于真实的值, 放大了 100 倍
 */
@Retention(RetentionPolicy.SOURCE)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPE
)
annotation class ValueLong

/**
 * 记账项目的 Service
 */
interface TallyService {

    companion object {
        val githubTestImageList = listOf(
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214160948.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214161522.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214161612.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214161649.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214161715.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214161824.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214161907.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214161921.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214161942.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214162002.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214162015.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214162026.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214162103.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214162114.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214162122.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214162131.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214162201.png",
            "https://raw.githubusercontent.com/xiaojinzi123/images/master/20211214162214.png",
        )
        val smTestImageList = listOf(
            "https://s2.loli.net/2022/01/01/rWMVEUtCyRIsL5v.png",
            "https://s2.loli.net/2022/01/01/ebEQvxMyiDY67CF.png",
            "https://s2.loli.net/2022/01/01/hw5PCTyrQnqmAR9.png",
            "https://s2.loli.net/2022/01/01/t5j7eNYlRapZLCK.png",
            "https://s2.loli.net/2022/01/01/GLZB62lqhI7oTME.png",
            "https://s2.loli.net/2022/01/01/htVBLCuvWYDk5KE.png",
            "https://s2.loli.net/2022/01/01/FZivXCdxhtWMqck.png",
            "https://s2.loli.net/2022/01/01/FzdOIHNSXGng1Us.png",
            "https://s2.loli.net/2022/01/01/gtGArisMfP4lvqe.png",
            "https://s2.loli.net/2022/01/01/DXgrJkEYI5BtGU3.png",
            "https://s2.loli.net/2022/01/01/pHavlkMPiCSUjn7.png",
            "https://s2.loli.net/2022/01/01/48HuosiGxtpvYKF.png",
            "https://s2.loli.net/2022/01/01/Nz1h6CW9ZuEKAyk.png",
            "https://s2.loli.net/2022/01/01/AbqWB25T64GuIeO.png",
            "https://s2.loli.net/2022/01/01/MwmQ9WxlGrB5tNV.png",
            "https://s2.loli.net/2022/01/01/6ArHQtWE3CODfG9.png",
            "https://s2.loli.net/2022/01/01/BxUGbICykn7sF68.png",
            "https://s2.loli.net/2022/01/01/I4hAWZDCl9RJmfg.png",
        )
        val testImageList = when(tallyAppService.imageType) {
            ImageUploadServerType.Github -> githubTestImageList
            ImageUploadServerType.Gitee -> smTestImageList
        }
    }

    /**
     * 数据库变化的通知
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val dataBaseChangedObservable: Flow<Unit>

    /**
     * 数据库变化的通知
     * 除了账户表
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val dataBaseChangedExceptAccountTableObservable: Flow<Unit>

    /**
     * 数据库变更执行一定的操作
     */
    fun <T> subscribeWithDataBaseChanged(callable: suspend () -> T): Flow<T>{
        return dataBaseChangedObservable
            .map {
                callable.invoke()
            }
    }

    /**
     * 是否初始化了测试数据
     */
    suspend fun isInitTestData(): Boolean

    /**
     * 初始化第一次安装的数据
     */
    suspend fun initDataIfNoData()

    /**
     * 标记初始化数据
     */
    suspend fun flagInitData(b: Boolean)

    /**
     * 初始化测试数据, 如果没有数据
     */
    suspend fun initTestDataIfNoData(isCreateBill: Boolean = true)

    /**
     * 标记初始化数据
     */
    suspend fun flagInitTestData(b: Boolean)

    /**
     * 事务中执行
     */
    suspend fun withTransaction(block: suspend () -> Unit)

}