package com.xiaojinzi.tally.statistical.module.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.TabType
import com.xiaojinzi.tally.base.service.statistical.DataStatisticalCostDTO
import com.xiaojinzi.tally.base.support.mainService
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.statistical.R

/**
 * 每月的统计的面板
 */
@Composable
fun MonthStatisticalView(
    costDTO: DataStatisticalCostDTO? = null,
    currentMonthlyBudgetBalance: Long? = null,
    currentMonthlyRemainBudget: Long? = null,
    monthTime: Long? = null,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                mainService.tabObservable.value = TabType.Statistical
            }
            .background(color = MaterialTheme.colors.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .nothing()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .nothing()
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .nothing(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_month_spending),
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                    textAlign = TextAlign.End,
                )
                Spacer(modifier = Modifier.height(height = 10.dp))
                Text(
                    text = costDTO?.realSpending?.tallyCostAdapter()?.tallyNumberFormat1()?: "---",
                    style = MaterialTheme.typography.body2.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
            }
            Column(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .nothing(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_month_income),
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
                Spacer(modifier = Modifier.size(width = 0.dp, height = 10.dp))
                Text(
                    text = costDTO?.realIncome?.tallyCostAdapter()?.tallyNumberFormat1()?: "---",
                    style = MaterialTheme.typography.body2.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
            }
            Column(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .wrapContentHeight()
                    .clickable {
                        Router.with(context)
                            .hostAndPath(TallyRouterConfig.TALLY_BILL_BUDGET)
                            .putLong(key = "monthTime", value = monthTime)
                            .forward()
                    }
                    .nothing(),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_monthly_budget),
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
                Spacer(modifier = Modifier.size(width = 0.dp, height = 10.dp))
                Text(
                    text = currentMonthlyBudgetBalance
                        ?.tallyCostAdapter()
                        ?.tallyNumberFormat1()
                        ?: "---",
                    style = MaterialTheme.typography.body2.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.height(height = 16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .nothing()
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .nothing(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_month_balance),
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
                Spacer(modifier = Modifier.size(width = 0.dp, height = 10.dp))
                Text(
                    text = costDTO?.balance?.tallyCostAdapter()?.tallyNumberFormat1()?: "---",
                    style = MaterialTheme.typography.h5.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
            }
            Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
            Column(
                modifier = Modifier
                    .clickable {
                        Router.with(context)
                            .hostAndPath(TallyRouterConfig.TALLY_BILL_BUDGET)
                            .putLong(key = "monthTime", value = monthTime)
                            .forward()
                    }
                    .wrapContentSize()
                    .nothing(),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_remaining_budget),
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
                Spacer(modifier = Modifier.size(width = 0.dp, height = 10.dp))
                Text(
                    text = if (currentMonthlyRemainBudget == null) {
                        "---"
                    } else {
                        currentMonthlyRemainBudget.tallyCostAdapter()
                            .tallyNumberFormat1()
                    },
                    style = MaterialTheme.typography.h5.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
fun MonthStatisticalViewPreview() {
    Box(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Min)
            .nothing(),
    ) {
        MonthStatisticalView(
            testMonthStatisticalVO()
        )
    }
}

fun testMonthStatisticalVO(): DataStatisticalCostDTO {
    return DataStatisticalCostDTO(
        startTime = System.currentTimeMillis(),
        endTime = System.currentTimeMillis(),
        income = 40000,
        spending = 18000,
        realSpending = 111,
        realIncome = 1111,
    )
}