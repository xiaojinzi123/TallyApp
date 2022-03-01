package com.xiaojinzi.module.base.view.compose.date

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xiaojinzi.module.base.support.LogKeyword
import com.xiaojinzi.module.base.support.getHourByTimeStamp
import com.xiaojinzi.module.base.support.getMinuteByTimeStamp
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.util.LogSupport
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalMaterialApi
@Composable
fun TimeWheelView(
    modifier: Modifier = Modifier,
    dateTimeTimeState: DateTimeWheelState = rememberDateTimeWheelState(initTime = System.currentTimeMillis()),
) {

    val hourWheelState: HourWheelState =
        rememberHourWheelState(initValue = getHourByTimeStamp(timeStamp = dateTimeTimeState.currentTime))
    val minuteWheelState: MinuteWheelState =
        rememberMinuteWheelState(initValue = getMinuteByTimeStamp(timeStamp = dateTimeTimeState.currentTime))

    val resultCalendar = Calendar.getInstance()
    resultCalendar.timeInMillis = dateTimeTimeState.currentTime
    resultCalendar.set(Calendar.HOUR_OF_DAY, hourWheelState.currentValue)
    resultCalendar.set(Calendar.MINUTE, minuteWheelState.currentValue)
    resultCalendar.set(Calendar.SECOND, 0)
    dateTimeTimeState.currentTime = resultCalendar.timeInMillis

    LogSupport.d(
        content = "dayStartTime = ${
            SimpleDateFormat("yyyy MM dd HH:mm:ss").format(
                Date(
                    dateTimeTimeState.currentTime
                )
            )
        }",
        keywords = arrayOf(LogKeyword.dateTime),
    )

    Row(
        modifier = modifier,
    ) {
        HourWheelView(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .nothing(),
            hourWheelState = hourWheelState,
        )
        MinuteWheelView(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .nothing(),
            minuteWheelState = minuteWheelState,
        )
    }

}