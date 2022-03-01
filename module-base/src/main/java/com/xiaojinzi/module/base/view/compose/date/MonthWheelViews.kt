package com.xiaojinzi.module.base.view.compose.date

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.xiaojinzi.module.base.R
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.getMonthByTimeStamp
import com.xiaojinzi.module.base.view.compose.WheelItem
import com.xiaojinzi.module.base.view.compose.WheelView
import com.xiaojinzi.module.base.view.compose.rememberWheelState

private val currentMonth: Int get() = getMonthByTimeStamp(timeStamp = System.currentTimeMillis())

// 0..11 可以对上月份的值
private val numberList = (0..11).toList()

private val monthRsdList = listOf(
    R.string.res_str_january,
    R.string.res_str_february,
    R.string.res_str_march,
    R.string.res_str_april,
    R.string.res_str_may,
    R.string.res_str_june,
    R.string.res_str_july,
    R.string.res_str_august,
    R.string.res_str_september,
    R.string.res_str_october,
    R.string.res_str_november,
    R.string.res_str_december,
)

@ExperimentalMaterialApi
@Composable
fun MonthWheelView(
    modifier: Modifier = Modifier,
    monthWheelState: MonthWheelState = rememberMonthWheelState(initValue = currentMonth),
) {
    val wheelState =
        rememberWheelState(initIndex = if (monthWheelState.currentValue in monthRsdList.indices) monthWheelState.currentValue else 0)
    LaunchedEffect(key1 = wheelState) {
        snapshotFlow { wheelState.currentIndex }
            .collect {
                if (it != monthWheelState.currentValue) {
                    monthWheelState.currentValue = it
                }
            }
    }
    WheelView(
        modifier = modifier,
        items = monthRsdList,
        wheelState = wheelState,
    ) {
        WheelItem(content = stringResource(id = it).toStringItemDTO())
    }
}

class MonthWheelState(currentValue: Int) {

    var currentValue by mutableStateOf(currentValue)

    companion object {
        val Saver = object : Saver<MonthWheelState, Int> {
            override fun restore(value: Int): MonthWheelState {
                return MonthWheelState(currentValue = value)
            }

            override fun SaverScope.save(value: MonthWheelState): Int {
                return value.currentValue
            }
        }
    }

}

@Composable
fun rememberMonthWheelState(
    initValue: Int = 0
) = rememberSaveable(saver = MonthWheelState.Saver) {
    MonthWheelState(currentValue = initValue)
}