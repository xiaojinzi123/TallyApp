package com.xiaojinzi.module.base.view.compose.date

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.getMinuteByTimeStamp
import com.xiaojinzi.module.base.view.compose.WheelItem
import com.xiaojinzi.module.base.view.compose.WheelView
import com.xiaojinzi.module.base.view.compose.rememberWheelState

private val currentMinute: Int get() = getMinuteByTimeStamp(timeStamp = System.currentTimeMillis())

private val numberList = (0..59).toList()

@ExperimentalMaterialApi
@Composable
fun MinuteWheelView(
    modifier: Modifier = Modifier,
    minuteWheelState: MinuteWheelState = rememberMinuteWheelState(initValue = currentMinute),
) {
    val initIndex = numberList.indexOf(element = minuteWheelState.currentValue)
    val wheelState = rememberWheelState(initIndex = if (initIndex > -1) initIndex else 0)
    LaunchedEffect(key1 = wheelState) {
        snapshotFlow { wheelState.currentIndex }
            .collect {
                if (numberList[it] != minuteWheelState.currentValue) {
                    minuteWheelState.currentValue = numberList[it]
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

class MinuteWheelState(currentValue: Int) {

    var currentValue by mutableStateOf(currentValue)

    companion object {
        val Saver = object : Saver<MinuteWheelState, Int> {
            override fun restore(value: Int): MinuteWheelState {
                return MinuteWheelState(currentValue = value)
            }

            override fun SaverScope.save(value: MinuteWheelState): Int {
                return value.currentValue
            }
        }
    }

}

@Composable
fun rememberMinuteWheelState(
    initValue: Int = 0
) = rememberSaveable(saver = MinuteWheelState.Saver) {
    MinuteWheelState(currentValue = initValue)
}