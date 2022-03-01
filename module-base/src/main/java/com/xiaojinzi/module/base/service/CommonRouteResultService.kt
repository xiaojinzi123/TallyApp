package com.xiaojinzi.module.base.service

import android.app.Activity
import android.content.Context
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.component.intentResultCodeMatchAwait
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.notSupportError

interface CommonRouteResultService {

    /**
     * 选择一个时间
     */
    suspend fun selectDateTime(
        context: Context,
        dateTime: Long = System.currentTimeMillis(),
        defTime: Long = System.currentTimeMillis()
    ): Long

    /**
     * 填写一个数字.
     */
    suspend fun fillInNumber(
        context: Context,
        value: Float? = null,
        def: Float = 0f,
    ): Float

    /**
     * 菜单选择
     */
    suspend fun menuSelect(
        context: Context,
        items: List<StringItemDTO>,
    ): Int

}

@ServiceAnno(CommonRouteResultService::class)
class CommonRouteResultServiceImpl : CommonRouteResultService {

    override suspend fun selectDateTime(context: Context, dateTime: Long, defTime: Long): Long {
        return Router.with(context)
            .hostAndPath(RouterConfig.SYSTEM_DATE_TIME_PICKER)
            .putLong("dateTime", dateTime)
            .requestCodeRandom()
            .intentAwait()
            .getLongExtra("dateTime", defTime)
    }

    override suspend fun fillInNumber(context: Context, value: Float?, def: Float): Float {
        return Router.with(context)
            .hostAndPath(RouterConfig.SYSTEM_FILL_IN_NUMBER)
            .apply {
                if (value != null) {
                    this.putFloat("data", value)
                }
            }
            .requestCodeRandom()
            .intentAwait()
            .getFloatExtra("data", def)
    }

    override suspend fun menuSelect(context: Context, items: List<StringItemDTO>): Int {
        val indexValue = Router.with(context)
            .hostAndPath(RouterConfig.SYSTEM_MENU_SELECT)
            .putParcelableArrayList("data", ArrayList(items))
            .requestCodeRandom()
            .intentResultCodeMatchAwait(expectedResultCode = Activity.RESULT_OK)
            .getIntExtra("data", Int.MIN_VALUE)
        if (Int.MIN_VALUE == indexValue) {
            notSupportError()
        }
        return indexValue
    }

}