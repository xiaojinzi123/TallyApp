package com.xiaojinzi.tally.statistical.module.core.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.module.base.theme.Yellow900
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.statistical.R

val tabTitleList = listOf(
    R.string.res_str_month,
    R.string.res_str_year,
)

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun StatisticsView() {
    val vm: CoreStatisticalViewModel = viewModel()
    val selectTabType by vm.tabTypeSelectObservableVO.collectAsState(initial = StatisticalTabType.Month)
    Column(
        modifier = Modifier
            .nothing(),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .nothing(),
        ) {
            Row(
                modifier = Modifier
                    .width(intrinsicSize = IntrinsicSize.Max)
                    // .background(color = Color.Yellow)
                    .statusBarsPadding()
                    .padding(top = 20.dp, bottom = 10.dp)
                    .padding(horizontal = 0.dp, vertical = 0.dp)
                    .nothing(),
                horizontalArrangement = Arrangement.Center,
            ) {
                tabTitleList.forEachIndexed { index, nameRsd ->
                    val isSelect = index == selectTabType.index
                    Text(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .clip(shape = RoundedCornerShape(percent = 20))
                            .clickable {
                                vm.tabTypeSelectObservableVO.value = StatisticalTabType.fromIndex(index = index)
                            }
                            .run {
                                if(isSelect) {
                                    this.background(
                                        color = Yellow900,
                                    )
                                } else {
                                    this
                                }
                            }
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .nothing(),
                        text = stringResource(id = nameRsd),
                        style = MaterialTheme.typography.body2.run {
                            if (isSelect) {
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

            }
        }

        when (selectTabType) {
            StatisticalTabType.Month -> MonthlyStatisticalView()
            StatisticalTabType.Year -> YearlyStatisticalView()
        }

    }
}