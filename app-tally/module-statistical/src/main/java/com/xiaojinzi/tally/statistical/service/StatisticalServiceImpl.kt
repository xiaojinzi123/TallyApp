package com.xiaojinzi.tally.statistical.service

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.tally.base.service.StatisticalService
import com.xiaojinzi.tally.statistical.module.account.view.AccountStatisticalOverviewType
import com.xiaojinzi.tally.statistical.module.account.view.AccountStatisticalOverviewViewWrap
import com.xiaojinzi.tally.statistical.module.account.view.AccountStatisticalView
import com.xiaojinzi.tally.statistical.module.calendar.view.CalendarStatisticalView
import com.xiaojinzi.tally.statistical.module.core.view.StatisticsView
import com.xiaojinzi.tally.statistical.module.home.view.HomeView

@ServiceAnno(StatisticalService::class)
class StatisticalServiceImpl : StatisticalService {

    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Composable
    override fun homeView() {
        HomeView()
    }

    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Composable
    override fun calendarView() {
        CalendarStatisticalView()
    }

    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @Composable
    override fun statisticalView() {
        StatisticsView()
    }

    @Composable
    override fun accountStatisticalView() {
        AccountStatisticalView()
    }

    @Composable
    override fun accountStatisticalOverviewViewForHome(clickJumpToAccountView: Boolean) {
        AccountStatisticalOverviewViewWrap(
            type = AccountStatisticalOverviewType.Home,
            clickJumpToAccountView = clickJumpToAccountView
        )
    }

    @Composable
    override fun accountStatisticalOverviewViewForMy(clickJumpToAccountView: Boolean) {
        AccountStatisticalOverviewViewWrap(
            type = AccountStatisticalOverviewType.My,
            clickJumpToAccountView = clickJumpToAccountView
        )
    }

}