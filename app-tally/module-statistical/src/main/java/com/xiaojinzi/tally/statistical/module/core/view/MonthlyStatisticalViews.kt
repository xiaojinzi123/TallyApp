package com.xiaojinzi.tally.statistical.module.core.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.view.compose.AppTabView
import com.xiaojinzi.module.base.view.compose.TabVO
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.base.view.BillDayListReportView
import com.xiaojinzi.tally.base.view.CateGroupCostPercentGroupVO
import com.xiaojinzi.tally.base.view.CateGroupCostPercentView
import com.xiaojinzi.tally.base.view.PieChartVO
import com.xiaojinzi.tally.base.view.PieChartView
import com.xiaojinzi.tally.base.view.toPieChartItemVO
import com.xiaojinzi.tally.statistical.R

@Composable
private fun ItemBg(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(shape = MaterialTheme.shapes.small)
            .background(color = MaterialTheme.colors.surface)
            .padding(vertical = 4.dp)
            .nothing(),
        content = content
    )
}

@Composable
private fun MonthlyCurrentMonthBill() {
    val vm: CoreStatisticalViewModel = viewModel()
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .clickable {
                vm.monthlyStatisticalUseCase.toMonthlyBillView(context = context)
            }
            .padding(vertical = 4.dp)
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

@Composable
private fun MonthlyStatisticalTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface
) {
    val targetWidth = 20.dp
    Spacer(
        modifier
            .width(width = targetWidth)
            .widthIn(min = targetWidth, max = targetWidth)
            .requiredWidthIn(min = targetWidth, max = targetWidth)
            .height(height = 2.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
            .nothing()
    )
}

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun MonthlyStatisticalView() {
    val vm: CoreStatisticalViewModel = viewModel()
    val context = LocalContext.current
    val targetSpendingData: CateGroupCostPercentGroupVO? by vm
        .selectMonthSpendingCostPercentVO.collectAsState(initial = null)
    val targetIncomeData: CateGroupCostPercentGroupVO? by vm
        .selectMonthIncomeCostPercentVO.collectAsState(initial = null)
    val currMonthBillList by vm.currBillListObservableVO.collectAsState(initial = emptyList())
    val selectYear by vm.monthlyStatisticalUseCase.selectYearObservableDTO.collectAsState(initial = -1)
    val selectIndex by vm.monthlyStatisticalUseCase.selectMonthObservableDTO.collectAsState(initial = 0)
    val monthCostTabSelectIndex by vm.monthCostTabSelectIndexObservableVO.collectAsState(initial = 0)
    val monthReport by vm.monthlyStatisticalUseCase.selectMonthReportObservableDTO.collectAsState(
        initial = Triple(0f, 0f, 0f))
    val tabIndicator = @Composable { tabPositions: List<TabPosition> ->
        MonthlyStatisticalTabIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectIndex])
        )
    }
    Column(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .nothing(),
    ) {
        // 月份的 tab 选择
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.surface,
                )
                .nothing(),
        ) {
            Text(
                modifier = Modifier
                    .clickable {
                        vm.monthlyStatisticalUseCase.toChooseYear(context = context)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .nothing(),
                text = selectYear.toString(),
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = Modifier.width(width = 16.dp))
            ScrollableTabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .nothing(),
                selectedTabIndex = selectIndex,
                indicator = tabIndicator,
                divider = {},
                edgePadding = 0.dp,
                backgroundColor = MaterialTheme.colors.surface,
            ) {
                (0..11).forEach { monthIndex ->
                    Tab(
                        selected = selectIndex == monthIndex,
                        onClick = {
                            vm.monthlyStatisticalUseCase.selectMonthObservableDTO.value = monthIndex
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .nothing(),
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .nothing(),
                            text = "${monthIndex + 1}月",
                            style = MaterialTheme.typography.caption,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //月度总计
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .verticalScroll(state = rememberScrollState())
                .nothing()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(shape = MaterialTheme.shapes.small)
                    .background(color = MaterialTheme.colors.primary)
                    .padding(vertical = 12.dp)
                    .nothing(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.res_str_monthly_spending),
                        style = MaterialTheme.typography.body2.copy(
                            color = MaterialTheme.colors.onPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    Text(
                        text = monthReport.first.tallyNumberFormat1(),
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onPrimary
                        ),
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.res_str_monthly_income),
                        style = MaterialTheme.typography.body2.copy(
                            color = MaterialTheme.colors.onPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    Text(
                        text = monthReport.second.tallyNumberFormat1(),
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onPrimary
                        ),
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.res_str_monthly_balance),
                        style = MaterialTheme.typography.body2.copy(
                            color = MaterialTheme.colors.onPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    Text(
                        text = monthReport.third.tallyNumberFormat1(),
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onPrimary
                        ),
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState())
                .padding(horizontal = 16.dp)
                .nothing(),
        ) {

            ItemBg {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 8.dp)
                        .nothing(),
                ) {
                    Spacer(modifier = Modifier.height(height = 0.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 0.dp)
                            .padding(top = 0.dp)
                            .nothing(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                        AppTabView(items = listOf(
                            R.string.res_str_spending.toStringItemDTO(),
                            R.string.res_str_income.toStringItemDTO(),
                        ).mapIndexed { index, item ->
                            TabVO(
                                isSelect = monthCostTabSelectIndex == index,
                                content = item
                            )
                        }) { index ->
                            vm.monthCostTabSelectIndexObservableVO.value = index
                        }
                        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                        MonthlyCurrentMonthBill()
                    }
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    // 支出和收入的进度条的百分比显示
                    when (monthCostTabSelectIndex) {
                        0 -> {
                            PieChartView(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 0.dp)
                                    .nothing(),
                                vo = PieChartVO(
                                    content = "测试内容 \n 100.00".toStringItemDTO(),
                                    items = (targetSpendingData?.items ?: emptyList()).map {
                                        it.toPieChartItemVO()
                                    },
                                )
                            )
                        }
                        1 -> {
                            PieChartView(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 0.dp)
                                    .nothing(),
                                vo = PieChartVO(
                                    content = "测试内容 \n 100.00".toStringItemDTO(),
                                    items = (targetIncomeData?.items ?: emptyList()).map {
                                        it.toPieChartItemVO()
                                    },
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(height = 0.dp))
                }
            }

            ItemBg {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nothing(),
                ) {
                    Spacer(modifier = Modifier.height(height = 8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 0.dp)
                            .padding(top = 0.dp)
                            .nothing(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                        AppTabView(items = listOf(
                            R.string.res_str_spending.toStringItemDTO(),
                            R.string.res_str_income.toStringItemDTO(),
                        ).mapIndexed { index, item ->
                            TabVO(
                                isSelect = monthCostTabSelectIndex == index,
                                content = item
                            )
                        }) { index ->
                            vm.monthCostTabSelectIndexObservableVO.value = index
                        }
                        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                        MonthlyCurrentMonthBill()
                    }
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    // 支出和收入的进度条的百分比显示
                    when (monthCostTabSelectIndex) {
                        0 -> {
                            CateGroupCostPercentView(
                                vos = targetSpendingData?.items ?: emptyList()
                            ) { _, item ->
                                vm.monthlyStatisticalUseCase.toCateGroupBillListView(
                                    context = context,
                                    cateGroupId = item.cateGroupId,
                                )
                            }
                        }
                        1 -> {
                            CateGroupCostPercentView(
                                vos = targetIncomeData?.items ?: emptyList()
                            ) { _, item ->
                                vm.monthlyStatisticalUseCase.toCateGroupBillListView(
                                    context = context,
                                    cateGroupId = item.cateGroupId,
                                )
                            }
                        }
                    }
                }
            }

            // 报表的展示
            ItemBg {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nothing(),
                ) {
                    BillDayListReportView(
                        vos = currMonthBillList
                    )
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Preview
@Composable
fun MonthlyStatisticalViewPreview() {
    MonthlyStatisticalView()
}