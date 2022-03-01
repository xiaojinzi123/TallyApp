package com.xiaojinzi.tally.develop.module.data_statistical.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.commonTimeFormat1
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.base.view.CommonActionItemView
import com.xiaojinzi.tally.base.view.CommonSurface1

@Composable
private fun DataStatisticalView() {
    val context = LocalContext.current
    val vm: DataStatisticalViewModel = viewModel()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nothing(),
    ) {

        CommonSurface1 {
            val dayTime by vm.dayTimeObservableDTO.collectAsState(initial = null)
            val dayTimeStr = dayTime?.commonTimeFormat1()
            val dayCost by vm.dayCostObservableDTO.collectAsState(initial = null)
            CommonActionItemView(
                contentNameItem = (
                        "时间：$dayTimeStr \n" +
                                "日支出：${(dayCost?.spending?: 0L).tallyCostAdapter().tallyNumberFormat1()}, " +
                                "日收入：${(dayCost?.income?: 0L).tallyCostAdapter().tallyNumberFormat1()}\n" +
                                "日真实支出：${(dayCost?.realSpending?: 0L).tallyCostAdapter().tallyNumberFormat1()}, " +
                                "日真实收入：${(dayCost?.realIncome?: 0L).tallyCostAdapter().tallyNumberFormat1()}"
                        ).toStringItemDTO()
            ) {
                vm.selectDayTime(context = context)
            }
            val monthTime by vm.monthTimeObservableDTO.collectAsState(initial = null)
            val monthTimeStr = monthTime?.commonTimeFormat1()
            val monthCost by vm.monthCostObservableDTO.collectAsState(initial = null)
            CommonActionItemView(
                contentNameItem = (
                        "时间：$monthTimeStr \n" +
                                "月支出：${(monthCost?.spending?: 0L).tallyCostAdapter().tallyNumberFormat1()}, " +
                                "月收入：${(monthCost?.income?: 0L).tallyCostAdapter().tallyNumberFormat1()}\n" +
                                "月真实支出：${(monthCost?.realSpending?: 0L).tallyCostAdapter().tallyNumberFormat1()}, " +
                                "月真实收入：${(monthCost?.realIncome?: 0L).tallyCostAdapter().tallyNumberFormat1()}"
                        ).toStringItemDTO()
            ) {
                vm.selectMonthTime(context = context)
            }
            val yearTime by vm.yearTimeObservableDTO.collectAsState(initial = null)
            val yearTimeStr = yearTime?.commonTimeFormat1()
            val yearCost by vm.yearCostObservableDTO.collectAsState(initial = null)
            CommonActionItemView(
                contentNameItem = (
                        "时间：$yearTimeStr \n" +
                                "月支出：${(yearCost?.spending?: 0L).tallyCostAdapter().tallyNumberFormat1()}, " +
                                "月收入：${(yearCost?.income?: 0L).tallyCostAdapter().tallyNumberFormat1()}\n" +
                                "月真实支出：${(yearCost?.realSpending?: 0L).tallyCostAdapter().tallyNumberFormat1()}, " +
                                "月真实收入：${(yearCost?.realIncome?: 0L).tallyCostAdapter().tallyNumberFormat1()}"
                        ).toStringItemDTO()
            ) {
                vm.selectYearTime(context = context)
            }
        }

    }
}

@Composable
fun DataStatisticalViewWrap() {
    Scaffold(
        topBar = {
            AppbarNormal(
                title = "数据统计"
            )
        }
    ) {
        DataStatisticalView()
    }
}

@Preview
@Composable
private fun DataStatisticalViewPreview() {
    DataStatisticalView()
}