package com.xiaojinzi.module.base.view.compose

import androidx.annotation.Keep
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaojinzi.module.base.R
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.module.base.theme.Yellow900
import com.xiaojinzi.support.ktx.nothing

private val dayRsdOfWeek = listOf(
    R.string.res_str_sunday1,
    R.string.res_str_monday1,
    R.string.res_str_tuesday1,
    R.string.res_str_wednesday1,
    R.string.res_str_thursday1,
    R.string.res_str_friday1,
    R.string.res_str_saturday1,
)

@Keep
data class CalendarItemVO<T1>(
    val isShow: Boolean,
    val isToday: Boolean = false,
    val isSelect: Boolean = false,
    val year: Int? = null,
    val month: Int? = null,
    val dayOfMonth: Int? = null,
    val additionalData1: T1? = null
)

// typealias CalendarItemVO = CalendarItemWithDataVO<*>

@Composable
fun CalendarContentItemView(vo: CalendarItemVO<*>) {
    Text(
        modifier = Modifier
            .wrapContentSize()
            .padding(vertical = 0.dp)
            .nothing(),
        text = vo.dayOfMonth.toString(),
        style = MaterialTheme.typography.body2.run {
            if (vo.isSelect) {
                this.copy(
                    color = Color.White
                )
            } else {
                this
            }
        },
        textAlign = TextAlign.Center,
    )
}

@ExperimentalFoundationApi
@Composable
fun <T1> CalendarView(
    vos: List<CalendarItemVO<T1>>,
    itemContent: (@Composable (item: CalendarItemVO<T1>) -> Unit) = {
        CalendarContentItemView(vo = it)
    },
    onItemLongClick: (vo: CalendarItemVO<T1>) -> Unit = {},
    onItemClick: (vo: CalendarItemVO<T1>) -> Unit = {},
) {
    GridView(
        items = vos,
        columnNumber = 7,
        headerContent = { index ->
            Text(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .nothing(),
                text = stringResource(id = dayRsdOfWeek[index]),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
        }
    ) { item ->
        if (item.isShow) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 1.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .run {
                        when {
                            item.isSelect -> {
                                this.background(
                                    color = Yellow900,
                                )
                            }
                            item.isToday -> {
                                this.background(
                                    color = Yellow900.copy(alpha = 0.2f),
                                )
                            }
                            else -> {
                                this
                            }
                        }
                    }
                    .combinedClickable(
                        onLongClick = {
                            onItemLongClick.invoke(item)
                        }
                    ) {
                        onItemClick.invoke(item)
                    }
                    .nothing(),
                contentAlignment = Alignment.TopCenter,
            ) {
                itemContent(item)
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun CalendarViewPreview() {
    CalendarView(
        vos = listOf<CalendarItemVO<Any>>(
            CalendarItemVO(isShow = false),
            CalendarItemVO(isShow = false),
        ) + (1..30).map {
            CalendarItemVO(
                isShow = true,
                isSelect = it == 10,
                dayOfMonth = it
            )
        }
    )
}