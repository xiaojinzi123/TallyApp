package com.xiaojinzi.tally.base.service

import androidx.compose.runtime.Composable

interface StatisticalService {

    /**
     * Home 页面
     */
    @Composable
    fun homeView()

    /**
     * Home 页面
     */
    @Composable
    fun calendarView()

    /**
     * 统计页面
     */
    @Composable
    fun statisticalView()

    /**
     * 账户统计页面
     */
    @Composable
    fun accountStatisticalView()

    /**
     * 首页的账户统计页面的概况视图
     */
    @Composable
    fun accountStatisticalOverviewViewForHome(clickJumpToAccountView: Boolean)

    /**
     * 我的界面的账户统计页面的概况视图
     */
    @Composable
    fun accountStatisticalOverviewViewForMy(clickJumpToAccountView: Boolean)

}