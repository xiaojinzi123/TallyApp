package com.xiaojinzi.tally.statistical.module.home.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.support.commonTimeFormat4
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.statistical.DataStatisticalCostDTO
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BillPageListView
import com.xiaojinzi.tally.statistical.R

@Composable
fun HomeTitleBarView() {
    val context = LocalContext.current
    val vm: HomeViewModel = viewModel()
    val time by vm.currentMonthTimeObservableDTO.collectAsState(initial = null)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp)
            .nothing(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .clickable {
                    vm.toChooseTime(context = context)
                }
                .nothing(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(
                modifier = Modifier
                    .width(0.dp)
                    .nothing(),
            )
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .nothing(),
                text = time?.commonTimeFormat4() ?: "---",
                style = MaterialTheme.typography.h6
            )
        }
        Spacer(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
        )
        Box(
            modifier = Modifier
                .clip(shape = CircleShape)
                .offset(x = 16.dp)
                .clickable {
                    Router.with(context)
                        .hostAndPath(TallyRouterConfig.TALLY_BILL_SEARCH)
                        .forward()
                }
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .nothing(),
        ) {
            Image(
                modifier = Modifier.size(size = 20.dp),
                painter = painterResource(id = R.drawable.res_search1),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(width = 0.dp))
        /*Image(
            modifier = Modifier
                .clickable {

                }
                .size(size = 20.dp),
            painter = painterResource(id = R.drawable.res_more1),
            contentDescription = null
        )*/
    }
}

@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun HomeView() {
    val vm: HomeViewModel = viewModel()
    val monthStatisticalDTO: DataStatisticalCostDTO? by vm
        .currMonthStatisticalObservableVO
        .collectAsState(initial = null)
    val currentMonthlyBudgetBalance by vm.currentMonthlyBudgetBalanceObservableDTO.collectAsState(
        initial = null
    )
    val currentMonthlyRemainBudget by vm.currentMonthlyRemainBudgetObservableDTO.collectAsState(
        initial = null
    )
    val monthTime by vm.currentMonthTimeObservableDTO.collectAsState(initial = null)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .nothing()
    ) {
        HomeTitleBarView()
        MonthStatisticalView(
            costDTO = monthStatisticalDTO,
            currentMonthlyBudgetBalance = currentMonthlyBudgetBalance,
            currentMonthlyRemainBudget = currentMonthlyRemainBudget,
            monthTime = monthTime,
        )
        Spacer(modifier = Modifier.height(12.dp))
        val billGroupVOList = vm.currBillPageListObservableVO.collectAsLazyPagingItems()
        BillPageListView(
            Modifier.weight(weight = 1f, fill = true),
            vos = billGroupVOList,
            isItemsRound = true,
            dayTitleHorizontalPadding = 0.dp,
        )
    }
}

@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Preview
@Composable
fun HomeViewPreview() {
    TallyTheme {
        StateBar {
            HomeView()
        }
    }
}