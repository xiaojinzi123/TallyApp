package com.xiaojinzi.tally.base.service

import androidx.annotation.Keep
import com.xiaojinzi.module.base.service.ImageUploadServerType
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.support.annotation.HotObservable
import kotlinx.coroutines.flow.Flow

/**
 * 各个渠道的包名
 * https://s2.loli.net/2022/01/04/Jh6c3S8QNGXlV1g.png
 */
@Keep
enum class AppChannelDTO(val channel: Int, val packageName: String?) {

    KUAN(
        channel = 1,
        packageName = "com.coolapk.market",
    ),
    GP(
        channel = 2,
        packageName = "com.android.vending",
    ),
    // 阿里应用平台分发
    ALI_APP_DISTRIBUTION(
        channel = 3,
        packageName = null,
    );
    companion object {
        fun parseChannel(value: Int): AppChannelDTO {
            return when (value) {
                KUAN.channel -> KUAN
                GP.channel -> GP
                ALI_APP_DISTRIBUTION.channel -> ALI_APP_DISTRIBUTION
                else -> notSupportError()
            }
        }
    }
}

interface TallyAppService {

    companion object {
        const val BASE_PACKAGE = "com.xiaojinzi.tally"
        const val PACKAGE_SELF = "$BASE_PACKAGE.self"
    }

    /**
     * 是否插入测试数据, 是控制的开关
     */
    val isInsertTestData: Boolean

    /**
     * 图片的类型
     */
    val imageType: ImageUploadServerType

    /**
     * 自动记账是否可用
     */
    val autoBillEnable: Boolean

    /**
     * bugly 是否可用
     */
    val buglyEnable: Boolean

    /**
     * bugly 的 appkey
     */
    val buglyAppkey: String

    /**
     * 渠道
     */
    val appChannel: AppChannelDTO

    /**
     * app 任务初始化的热流, 是 Publish 模式的
     */
    @HotObservable(HotObservable.Pattern.PUBLISH, isShared = true)
    val appTaskInitPublishObservableDTO: Flow<Unit>

    /**
     * app 任务初始化的热流, 是 Behavior 模式的
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val appTaskInitBehaviorObservableDTO: Flow<Unit>

    /**
     * 开始初始化任务
     */
    fun startInitTask()

}