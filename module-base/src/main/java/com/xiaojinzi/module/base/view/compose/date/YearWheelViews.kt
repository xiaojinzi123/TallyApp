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
import com.xiaojinzi.module.base.support.getYearByTimeStamp
import com.xiaojinzi.module.base.view.compose.WheelItem
import com.xiaojinzi.module.base.view.compose.WheelView
import com.xiaojinzi.module.base.view.compose.rememberWheelState

private val currentYear: Int get() = getYearByTimeStamp(timeStamp = System.currentTimeMillis())

// private val numberList = (2000..getYearByTimeStamp(timeStamp = System.currentTimeMillis())).toList()
private val numberList = (2000..2100).toList()

@ExperimentalMaterialApi
@Composable
fun YearWheelView(
    modifier: Modifier = Modifier,
    yearWheelState: YearWheelState = rememberYearWheelState(initValue = currentYear),
) {
    val initIndex = numberList.indexOf(element = yearWheelState.currentValue)
    val wheelState = rememberWheelState(initIndex = if (initIndex > -1) initIndex else 0)
    LaunchedEffect(key1 = wheelState) {
        snapshotFlow { wheelState.currentIndex }
            .collect {
                if (numberList[it] != yearWheelState.currentValue) {
                    yearWheelState.currentValue = numberList[it]
                }
            }
    }
    WheelView(
        modifier = modifier,
        items = numberList,
        wheelState = wheelState,
    ) {
        WheelItem(
            content = (it.toString() + stringResource(id = R.string.res_str_year1)).toStringItemDTO()
        )
    }
}

class YearWheelState(currentValue: Int) {

    var currentValue by mutableStateOf(currentValue)

    companion object {
        val Saver = object : Saver<YearWheelState, Int> {
            override fun restore(value: Int): YearWheelState {
                return YearWheelState(currentValue = value)
            }

            override fun SaverScope.save(value: YearWheelState): Int {
                return value.currentValue
            }
        }
    }

}

@Composable
fun rememberYearWheelState(
    initValue: Int = 0
) = rememberSaveable(saver = YearWheelState.Saver) {
    YearWheelState(currentValue = initValue)
}