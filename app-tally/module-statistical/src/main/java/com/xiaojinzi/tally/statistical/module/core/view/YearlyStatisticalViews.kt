package com.xiaojinzi.tally.statistical.module.core.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.view.compose.AppTabView
import com.xiaojinzi.module.base.view.compose.TabVO
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.base.view.*
import com.xiaojinzi.tally.statistical.R

@Composable
private fun ItemBg(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.small)
            .background(color = MaterialTheme.colors.surface)
            .padding(vertical = 4.dp)
            .nothing(),
        content = content
    )
}

@Composable
private fun YearlyStatisticalTabIndicator(
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
@Composable
fun YearlyStatisticalView() {
    val context = LocalContext.current
    val vm: CoreStatisticalViewModel = viewModel()
    val yearlyStatisticalVO by vm.yearlyStatisticalObservableVO.collectAsState(initial = null)
    val yearCostTabSelectIndex by vm.yearCostTabSelectIndexObservableVO.collectAsState(initial = 0)
    val targetSpendingData: CateGroupCostPercentGroupVO? by vm
        .selectYearSpendingCostPercentVO.collectAsState(initial = null)
    val targetIncomeData: CateGroupCostPercentGroupVO? by vm
        .selectYearIncomeCostPercentVO.collectAsState(initial = null)
    val selectIndex by vm.yearlyStatisticalUseCase
        .selectYearIndexObservableVO.collectAsState(initial = 0)
    val selectYearMonthReportList by vm.selectYearMonthReportListObservableVO.collectAsState(initial = emptyList())
    val tabIndicator = @Composable { tabPositions: List<TabPosition> ->
        YearlyStatisticalTabIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectIndex])
        )
    }
    Column(
        modifier = Modifier
            .nothing(),
    ) {

        // 年份的 tab
        ScrollableTabRow(
            selectedTabIndex = selectIndex,
            indicator = tabIndicator,
            divider = {},
            edgePadding = 0.dp,
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier,
        ) {
            vm.tabYearListVO.forEachIndexed { index, item ->
                Tab(
                    selected = selectIndex == index,
                    onClick = {
                        vm.yearlyStatisticalUseCase.selectYearIndexObservableVO.value = index
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
                        text = "$item 年",
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(height = 16.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .verticalScroll(state = rememberScrollState())
                .nothing(),
        ) {

            // 年度概况统计
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(shape = MaterialTheme.shapes.small)
                    .background(color = MaterialTheme.colors.primary)
                    .padding(horizontal = 0.dp, vertical = 12.dp)
                    .nothing(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .wrapContentHeight()
                        .nothing(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = R.string.res_str_yearly_spending),
                        style = MaterialTheme.typography.body2.copy(
                            color = MaterialTheme.colors.onPrimary
                        ),
                    )
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    Text(
                        text = (yearlyStatisticalVO?.spendingAdapter ?: 0f).tallyNumberFormat1(),
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onPrimary
                        ),
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .wrapContentHeight()
                        .nothing(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = R.string.res_str_yearly_income),
                        style = MaterialTheme.typography.body2.copy(
                            color = MaterialTheme.colors.onPrimary
                        ),
                    )
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    Text(
                        text = (yearlyStatisticalVO?.incomeAdapter ?: 0f).tallyNumberFormat1(),
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onPrimary
                        ),
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .wrapContentHeight()
                        .nothing(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = R.string.res_str_yearly_balance),
                        style = MaterialTheme.typography.body2.copy(
                            color = MaterialTheme.colors.onPrimary
                        ),
                    )
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    Text(
                        text = (yearlyStatisticalVO?.balanceAdapter ?: 0f).tallyNumberFormat1(),
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onPrimary
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(height = 10.dp))

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
                                isSelect = yearCostTabSelectIndex == index,
                                content = item
                            )
                        }) { index ->
                            vm.yearCostTabSelectIndexObservableVO.value = index
                        }
                        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                    }
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    // 支出和收入的进度条的百分比显示
                    when (yearCostTabSelectIndex) {
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

            Spacer(modifier = Modifier.height(height = 10.dp))

            ItemBg {
                Column(
                    modifier = Modifier
                        .clip(shape = MaterialTheme.shapes.small)
                        .background(color = MaterialTheme.colors.surface)
                        .padding(horizontal = 0.dp, vertical = 8.dp)
                        .nothing(),
                ) {

                    // 支出和收入的进度条的百分比显示
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 0.dp)
                            .padding(top = 0.dp)
                            .nothing(),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        // Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                        AppTabView(items = listOf(
                            R.string.res_str_spending.toStringItemDTO(),
                            R.string.res_str_income.toStringItemDTO(),
                        ).mapIndexed { index, item ->
                            TabVO(
                                isSelect = yearCostTabSelectIndex == index,
                                content = item
                            )
                        }) { index ->
                            vm.yearCostTabSelectIndexObservableVO.value = index
                        }
                    }
                    when (yearCostTabSelectIndex) {
                        0 -> Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .nothing(),
                        ) {
                            CateGroupCostPercentView(
                                vos = targetSpendingData?.items ?: emptyList()
                            ) { _, item ->
                                vm.yearlyStatisticalUseCase.toCateGroupBillListView(
                                    context = context,
                                    cateGroupId = item.cateGroupId,
                                )
                            }
                        }
                        1 -> Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .nothing(),
                        ) {
                            CateGroupCostPercentView(
                                vos = targetIncomeData?.items ?: emptyList()
                            ) { _, item ->
                                vm.yearlyStatisticalUseCase.toCateGroupBillListView(
                                    context = context,
                                    cateGroupId = item.cateGroupId,
                                )
                            }
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(height = 16.dp))

            // 报表的展示
            ItemBg {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nothing(),
                ) {
                    BillMonthListReportView(
                        vos = selectYearMonthReportList
                    )
                }
            }
        }

    }


}