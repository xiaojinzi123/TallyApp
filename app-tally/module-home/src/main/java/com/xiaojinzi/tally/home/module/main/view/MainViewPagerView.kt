package com.xiaojinzi.tally.home.module.main.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.support.myService
import com.xiaojinzi.tally.base.support.statisticalService

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
@ExperimentalPagerApi
fun MainViewPagerView(pageIndex: Int) {

    var size by remember { mutableStateOf(IntSize.Zero) }
    val sizeWidthDp: Dp = LocalDensity.current.run {
        size.width.toDp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                size = it
            }
            .nothing(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .offset(x = sizeWidthDp * (0 - pageIndex))
                .nothing()
        ) {
            statisticalService.homeView()
        }
        Column(
            Modifier
                .fillMaxSize()
                .offset(x = sizeWidthDp * (1 - pageIndex))
                .nothing()
        ) {
            statisticalService.calendarView()
        }
        Column(
            Modifier
                .fillMaxSize()
                .offset(x = sizeWidthDp * (2 - pageIndex))
                .nothing()
        ) {
            statisticalService.statisticalView()
        }
        Column(
            Modifier
                .fillMaxSize()
                .offset(x = sizeWidthDp * (3 - pageIndex))
                .nothing()
        ) {
            myService.myView()
        }
        /*when (pageIndex) {
            0 -> {
                statisticalService.homeView()
            }
            1 -> {
                statisticalService.calendarView()
            }
            2 -> {
                statisticalService.statisticalView()
            }
            3 -> {
                myService.myView()
            }
            else -> notSupportError()
        }*/
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Preview
@Composable
@ExperimentalPagerApi
fun viewPagerViewPreview() {
    MainViewPagerView(pageIndex = 1)
}