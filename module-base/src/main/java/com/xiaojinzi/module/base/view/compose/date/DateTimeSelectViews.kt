package com.xiaojinzi.module.base.view.compose.date

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xiaojinzi.support.ktx.nothing

enum class SelectType {
    Date, Time
}

@ExperimentalMaterialApi
@Composable
fun DateTimeSelectView(
    modifier: Modifier = Modifier,
    dateTimeTimeState: DateTimeWheelState = rememberDateTimeWheelState(initTime = System.currentTimeMillis()),
    selectType: SelectType = SelectType.Date,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .nothing(),
    ) {
        when (selectType) {
            SelectType.Date -> {
                DateWheelView(
                    dateTimeTimeState = dateTimeTimeState,
                )
            }
            SelectType.Time -> {
                TimeWheelView(
                    dateTimeTimeState = dateTimeTimeState,
                )
            }
        }
    }
}