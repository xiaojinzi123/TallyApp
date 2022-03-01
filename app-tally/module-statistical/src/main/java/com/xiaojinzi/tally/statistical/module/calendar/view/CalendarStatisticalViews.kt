package com.xiaojinzi.tally.statistical.module.calendar.view

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.support.LogUtil
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.module.base.view.compose.CalendarContentItemView
import com.xiaojinzi.module.base.view.compose.CalendarView
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.base.theme.body3
import com.xiaojinzi.tally.base.view.BillPageListView
import com.xiaojinzi.tally.statistical.R
import java.util.*

@ExperimentalMaterialApi
@SuppressLint("Range")
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun CalendarStatisticalView() {
    val context = LocalContext.current
    val vm: CalendarStatisticalViewModel = viewModel()
    LogUtil.loge("日历", "vm.pageIndexOffset1 = " + vm.pageIndexOffset)
    val preMonthList by vm.pageMonthCalendarItemListObservableVO[0].collectAsState(initial = emptyList())
    val currMonthList by vm.pageMonthCalendarItemListObservableVO[1].collectAsState(initial = emptyList())
    val nextMonthList by vm.pageMonthCalendarItemListObservableVO[2].collectAsState(initial = emptyList())
    val monthList1 = preMonthList.joinToString(separator = ",") { "${it.month}" }
    val monthList2 = currMonthList.joinToString(separator = ",") { "${it.month}" }
    val monthList3 = nextMonthList.joinToString(separator = ",") { "${it.month}" }
    LogUtil.loge("日历", "MonthListVO.monthList1 = $monthList1")
    LogUtil.loge("日历", "MonthListVO.monthList2 = $monthList2")
    LogUtil.loge("日历", "MonthListVO.monthList3 = $monthList3")
    val pageMonthDataVOList = listOf(
        preMonthList, currMonthList, nextMonthList
    )
    val preMonthDateTime by vm.pageMonthTimeListObservableVO[0].collectAsState(initial = System.currentTimeMillis())
    val currMonthDateTime by vm.pageMonthTimeListObservableVO[1].collectAsState(initial = System.currentTimeMillis())
    val nextMonthDateTime by vm.pageMonthTimeListObservableVO[2].collectAsState(initial = System.currentTimeMillis())
    val pageMonthDateTimeVOList = listOf(
        preMonthDateTime, currMonthDateTime, nextMonthDateTime
    )
    val preMonthStr by vm.pageMonthTimeStrListObservableVO[0].collectAsState(initial = "")
    val currMonthStr by vm.pageMonthTimeStrListObservableVO[1].collectAsState(initial = "")
    val nextMonthStr by vm.pageMonthTimeStrListObservableVO[2].collectAsState(initial = "")
    val pageMonthStrVOList = listOf(
        preMonthStr, currMonthStr, nextMonthStr
    )
    // 选中的日期的所有账单
    val selectDayBillList = vm.currBillPageListObservableVO.collectAsLazyPagingItems()
    // 选择的那天的时间戳
    val selectDayTime by vm.dateTimeOfDaySelectDTO.collectAsState(initial = System.currentTimeMillis())
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .statusBarsPadding()
            .padding(top = 16.dp)
            .nothing(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(state = rememberScrollState())
                .nothing(),
        ) {
            val pageState = rememberPagerState(
                initialPage = CalendarStatisticalViewModel.PAGE_COUNT * 500 + CalendarStatisticalViewModel.PAGE_INIT,
            )
            // 设置新的页面 Index
            vm.setNewPageIndex(targetPageIndex = pageState.currentPage % CalendarStatisticalViewModel.PAGE_COUNT)
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .nothing(),
                count = CalendarStatisticalViewModel.PAGE_COUNT * 1000,
                state = pageState,
                verticalAlignment = Alignment.Top,
            ) { pageIndex ->
                val realPageIndex = pageIndex % CalendarStatisticalViewModel.PAGE_COUNT
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .nothing(),
                ) {
                    // 日历视图上面的 header
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .nothing(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = when (realPageIndex) {
                                in (0..2) -> pageMonthStrVOList[realPageIndex]
                                else -> {
                                    notSupportError()
                                }
                            },
                            style = MaterialTheme.typography.subtitle1,
                        )
                        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                        Row(
                            modifier = Modifier
                                .clickable {
                                    Router.with(context)
                                        .hostAndPath(TallyRouterConfig.TALLY_BILL_MONTHLY)
                                        .putLong(
                                            "timestamp", pageMonthDateTimeVOList[realPageIndex]
                                        )
                                        .forward()
                                }
                                .nothing(),
                        ) {
                            Text(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .nothing(),
                                text = stringResource(id = R.string.res_str_monthly_bills),
                                style = MaterialTheme.typography.body2.copy(
                                    color = MaterialTheme.colors.secondary
                                ),
                            )
                            Spacer(modifier = Modifier.width(width = 0.dp))
                            Icon(
                                modifier = Modifier
                                    .size(size = 16.dp)
                                    .nothing(),
                                painter = painterResource(id = R.drawable.res_arrow_right1),
                                contentDescription = null,
                                tint = MaterialTheme.colors.secondary,
                            )
                        }
                    }
                    // 日历视图
                    CalendarView(
                        vos = when (realPageIndex) {
                            in (0..2) -> pageMonthDataVOList[realPageIndex]
                            else -> notSupportError()
                        },
                        itemContent = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .nothing(),
                            ) {
                                CalendarContentItemView(vo = it)
                                Spacer(modifier = Modifier.height(height = 2.dp))
                                val targetCost = it.additionalData1?.cost ?: 0f
                                val isMoreThanZero = targetCost > 0f
                                val targetCostStr = if (targetCost == 0f) {
                                    ""
                                } else {
                                    (if (isMoreThanZero) "+" else "") + (it.additionalData1?.cost?.tallyNumberFormat1() ?: "")
                                }
                                Text(
                                    text = targetCostStr,
                                    style = MaterialTheme.typography.body3.copy(
                                        color = if (it.isSelect) {
                                            Color.White
                                        } else {
                                            Color.Red
                                        }
                                    ),
                                )
                            }
                        },
                        onItemLongClick = {
                            if (it.isShow) {
                                Router
                                    .with(context)
                                    .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
                                    .putLong(
                                        "time",
                                        Calendar.getInstance()
                                            .apply {
                                                this.set(Calendar.YEAR, it.year!!)
                                                this.set(Calendar.MONTH, it.month!!)
                                                this.set(Calendar.DAY_OF_MONTH, it.dayOfMonth!!)
                                            }
                                            .timeInMillis
                                    )
                                    .forward()
                            }
                        }
                    ) {
                        if (it.isShow) {
                            vm.yearDTO.value = it.year!!
                            vm.monthDTO.value = it.month!!
                            vm.dayOfMonthDTO.value = it.dayOfMonth!!
                        }
                    }
                }
            }
        }
        // 账单视图
        BillPageListView(
            vos = selectDayBillList,
            emptyTipContent = stringResource(id = R.string.res_str_bill_of_day_is_empty_tip1),
            onEmptyClick = {
                Router
                    .with(context)
                    .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
                    .putLong("time", selectDayTime)
                    .forward()
            }
        )
    }
}