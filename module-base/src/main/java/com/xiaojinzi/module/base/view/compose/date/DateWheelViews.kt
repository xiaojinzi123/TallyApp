package com.xiaojinzi.module.base.view.compose.date

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.LogKeyword
import com.xiaojinzi.module.base.support.getMonthByTimeStamp
import com.xiaojinzi.module.base.support.getMonthInterval
import com.xiaojinzi.module.base.support.getYearByTimeStamp
import com.xiaojinzi.module.base.view.compose.WheelItem
import com.xiaojinzi.module.base.view.compose.WheelView
import com.xiaojinzi.module.base.view.compose.rememberWheelState
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.util.LogSupport
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalMaterialApi
@Composable
fun DateWheelView(
    modifier: Modifier = Modifier,
    dateTimeTimeState: DateTimeWheelState = rememberDateTimeWheelState(initTime = System.currentTimeMillis()),
) {

    val yearWheelState: YearWheelState =
        rememberYearWheelState(initValue = getYearByTimeStamp(timeStamp = dateTimeTimeState.currentTime))
    val monthWheelState: MonthWheelState =
        rememberMonthWheelState(initValue = getMonthByTimeStamp(timeStamp = dateTimeTimeState.currentTime))

    val resultCalendar = Calendar.getInstance()
    resultCalendar.timeInMillis = dateTimeTimeState.currentTime
    // 先重置为1, 避免干扰
    resultCalendar.set(Calendar.DAY_OF_MONTH, 1)
    resultCalendar.set(Calendar.YEAR, yearWheelState.currentValue)
    resultCalendar.set(Calendar.MONTH, monthWheelState.currentValue)

    LogSupport.d(
        content = "resultCalendar1 = ${SimpleDateFormat("yyyy MM dd HH:mm:ss").format(resultCalendar.time)}",
        keywords = arrayOf(LogKeyword.dateTime),
    )

    val tempCalendar = Calendar.getInstance()

    // 计算出当前时间的 day 的范围, 比如：1..31
    val (dayStartTime, dayEndTime) = getMonthInterval(timeStamp = resultCalendar.timeInMillis)
    tempCalendar.timeInMillis = dayStartTime
    val dayStart = tempCalendar.get(Calendar.DAY_OF_MONTH)
    tempCalendar.timeInMillis = dayEndTime
    val dayEnd = tempCalendar.get(Calendar.DAY_OF_MONTH)

    // 计算当前时间的 day
    tempCalendar.timeInMillis = dateTimeTimeState.currentTime
    val targetDay = tempCalendar.get(Calendar.DAY_OF_MONTH)

    val dayWheelState = rememberWheelState(initIndex = targetDay - 1)
    dayWheelState.currentIndex =
        dayWheelState.currentIndex.coerceIn(minimumValue = 0, maximumValue = dayEnd - 1)

    // 最终计算时间戳, 保存
    resultCalendar.set(Calendar.DAY_OF_MONTH, dayWheelState.currentIndex + 1)
    dateTimeTimeState.currentTime = resultCalendar.timeInMillis

    // 日志
    run {
        LogSupport.d(
            content = "dayStartTime = ${
                SimpleDateFormat("yyyy MM dd HH:mm:ss").format(
                    Date(
                        dayStartTime
                    )
                )
            }",
            keywords = arrayOf(LogKeyword.dateTime),
        )
        LogSupport.d(
            content = "dayEndTime = ${SimpleDateFormat("yyyy MM dd HH:mm:ss").format(Date(dayEndTime))}",
            keywords = arrayOf(LogKeyword.dateTime),
        )
        LogSupport.d(
            content = "year = ${yearWheelState.currentValue}",
            keywords = arrayOf(LogKeyword.dateTime),
        )
        LogSupport.d(
            content = "month = ${monthWheelState.currentValue}",
            keywords = arrayOf(LogKeyword.dateTime),
        )
        LogSupport.d(
            content = "dayStart = $dayStart",
            keywords = arrayOf(LogKeyword.dateTime),
        )
        LogSupport.d(
            content = "dayEnd = $dayEnd",
            keywords = arrayOf(LogKeyword.dateTime),
        )
        LogSupport.d(
            content = "wheelState.currentIndex = ${dayWheelState.currentIndex}",
            keywords = arrayOf(LogKeyword.dateTime),
        )
        LogSupport.d(
            content = "tempCalendar.timeInMillis = ${
                SimpleDateFormat("yyyy MM dd HH:mm:ss").format(
                    Date(tempCalendar.timeInMillis)
                )
            }",
            keywords = arrayOf(LogKeyword.dateTime),
        )
    }
    Row(
        modifier = modifier
    ) {
        YearWheelView(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .nothing(),
            yearWheelState = yearWheelState,
        )
        MonthWheelView(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .nothing(),
            monthWheelState = monthWheelState,
        )
        WheelView(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .nothing(),
            items = (dayStart..dayEnd).toList(),
            wheelState = dayWheelState,
        ) {
            WheelItem(content = it.toString().toStringItemDTO())
        }
    }

}

class DateTimeWheelState(currentTime: Long) {

    var currentTime by mutableStateOf(currentTime)

    companion object {
        val Saver = object : Saver<DateTimeWheelState, Long> {
            override fun restore(value: Long): DateTimeWheelState {
                return DateTimeWheelState(currentTime = value)
            }

            override fun SaverScope.save(value: DateTimeWheelState): Long {
                return value.currentTime
            }
        }
    }

}

@Composable
fun rememberDateTimeWheelState(
    initTime: Long = 0
) = rememberSaveable(saver = DateTimeWheelState.Saver) {
    DateTimeWheelState(currentTime = initTime)
}