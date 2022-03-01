package com.xiaojinzi.module.base.view.compose.date

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.getHourByTimeStamp
import com.xiaojinzi.module.base.view.compose.WheelItem
import com.xiaojinzi.module.base.view.compose.WheelView
import com.xiaojinzi.module.base.view.compose.rememberWheelState

private val currentHour: Int get() = getHourByTimeStamp(timeStamp = System.currentTimeMillis())

private val numberList = (0..23).toList()

@ExperimentalMaterialApi
@Composable
fun HourWheelView(
    modifier: Modifier = Modifier,
    hourWheelState: HourWheelState = rememberHourWheelState(initValue = currentHour),
) {
    val initIndex = numberList.indexOf(element = hourWheelState.currentValue)
    val wheelState = rememberWheelState(initIndex = if (initIndex > -1) initIndex else 0)
    LaunchedEffect(key1 = wheelState) {
        snapshotFlow { wheelState.currentIndex }
            .collect {
                if (numberList[it] != hourWheelState.currentValue) {
                    hourWheelState.currentValue = numberList[it]
                }
            }
    }
    WheelView(
        modifier = modifier,
        items = numberList,
        wheelState = wheelState,
    ) {
        WheelItem(content = it.toString().toStringItemDTO())
    }
}

class HourWheelState(currentValue: Int) {

    var currentValue by mutableStateOf(currentValue)

    companion object {
        val Saver = object : Saver<HourWheelState, Int> {
            override fun restore(value: Int): HourWheelState {
                return HourWheelState(currentValue = value)
            }

            override fun SaverScope.save(value: HourWheelState): Int {
                return value.currentValue
            }
        }
    }

}

@Composable
fun rememberHourWheelState(
    initValue: Int = 0
) = rememberSaveable(saver = HourWheelState.Saver) {
    HourWheelState(currentValue = initValue)
}