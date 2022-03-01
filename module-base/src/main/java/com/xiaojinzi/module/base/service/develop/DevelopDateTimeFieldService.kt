package com.xiaojinzi.module.base.service.develop

import android.content.Context
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

interface DevelopDateTimeFieldService {

    /**
     * 时间戳
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val timeStampObservableDTO: MutableSharedStateFlow<Long>

    /**
     * 年份
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val yearObservableDTO: Flow<Int>

    /**
     * 月份
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val monthObservableDTO: Flow<Int>

    /**
     * 一星期中的天
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val dayOfWeekObservableDTO: Flow<Int>

    /**
     * 月中一星期中的天
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val dayOfWeekInMonthObservableDTO: Flow<Int>

    /**
     * 月中的天
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val dayOfMonthObservableDTO: Flow<Int>

    /**
     * 年中的天
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val dayOfYearObservableDTO: Flow<Int>

    /**
     * 小时
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val hourObservableDTO: Flow<Int>

    /**
     * 一天中的小时
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val hourOfDayObservableDTO: Flow<Int>

    /**
     * 分钟
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val minuteObservableDTO: Flow<Int>

    /**
     * 秒
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val secondObservableDTO: Flow<Int>

    /**
     * 毫秒
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val millisecondObservableDTO: Flow<Int>

    /**
     * 去选择时间
     */
    @DelicateCoroutinesApi
    fun toChooseDateTime(context: Context) {
        AppScope.launch(ErrorIgnoreContext) {
            val resultDateTime = Router.with(context)
                .hostAndPath(RouterConfig.SYSTEM_DATE_TIME_PICKER)
                .putLong("dateTime", timeStampObservableDTO.value)
                .requestCodeRandom()
                .intentAwait()
                .getLongExtra("dateTime", System.currentTimeMillis())
            timeStampObservableDTO.value = resultDateTime
        }
    }

}

@ServiceAnno(DevelopDateTimeFieldService::class)
class DevelopDateTimeFieldServiceImpl : DevelopDateTimeFieldService {

    override val timeStampObservableDTO = MutableSharedStateFlow(initValue = System.currentTimeMillis())

    private val calendarObservableDTO = timeStampObservableDTO.map { timeInMillis ->
        Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
        }
    }

    override val yearObservableDTO = calendarObservableDTO.map { cal ->
        cal.get(Calendar.YEAR)
    }

    override val monthObservableDTO= calendarObservableDTO.map { cal ->
        cal.get(Calendar.MONTH)
    }

    override val dayOfWeekObservableDTO= calendarObservableDTO.map { cal ->
        cal.get(Calendar.DAY_OF_WEEK)
    }

    override val dayOfWeekInMonthObservableDTO= calendarObservableDTO.map { cal ->
        cal.get(Calendar.DAY_OF_WEEK_IN_MONTH)
    }

    override val dayOfMonthObservableDTO= calendarObservableDTO.map { cal ->
        cal.get(Calendar.DAY_OF_MONTH)
    }

    override val dayOfYearObservableDTO= calendarObservableDTO.map { cal ->
        cal.get(Calendar.DAY_OF_YEAR)
    }

    override val hourObservableDTO= calendarObservableDTO.map { cal ->
        cal.get(Calendar.HOUR)
    }

    override val hourOfDayObservableDTO= calendarObservableDTO.map { cal ->
        cal.get(Calendar.HOUR_OF_DAY)
    }

    override val minuteObservableDTO= calendarObservableDTO.map { cal ->
        cal.get(Calendar.MINUTE)
    }

    override val secondObservableDTO= calendarObservableDTO.map { cal ->
        cal.get(Calendar.SECOND)
    }

    override val millisecondObservableDTO= calendarObservableDTO.map { cal ->
        cal.get(Calendar.MILLISECOND)
    }


}