package com.xiaojinzi.tally.bill.module.budget.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.module.base.theme.Yellow900
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.module.base.view.compose.CommonCircularProgressIndicator
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.support.tallyBudgetService
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.base.theme.h7
import com.xiaojinzi.tally.bill.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
private fun BudgetBoardView(vo: MonthBudgetBoardVO?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp)
            .clip(shape = MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colors.primary,
            )
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .nothing(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                    text = stringResource(id = R.string.res_str_monthly_budget),
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = Color.White,
                    ),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                Text(
                    text = vo?.value?.tallyNumberFormat1() ?: "---",
                    style = MaterialTheme.typography.h7.copy(
                        color = Color.White,
                    ),
                )
            }

            Spacer(modifier = Modifier.width(width = 20.dp))

            Column(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .wrapContentHeight()
                    .nothing(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_remaining_budget),
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = Color.White,
                    ),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                Text(
                    text = vo?.valueRemain?.tallyNumberFormat1() ?: "---",
                    style = MaterialTheme.typography.h7.copy(
                        color = Color.White,
                    ),
                )
            }

            Spacer(modifier = Modifier.width(width = 20.dp))

            Box(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .wrapContentHeight()
                    .nothing(),
                contentAlignment = Alignment.Center,
            ) {
                val targetProgress by animateFloatAsState(
                    targetValue = vo?.valueRemainPercent ?: 0f
                )
                CommonCircularProgressIndicator(
                    modifier = Modifier
                        .nothing(),
                    progress = targetProgress,
                    backgroundColor = Color.White,
                    color = Yellow900,
                )
            }

        }
    }
}

@Composable
private fun BudgetView() {
    val context = LocalContext.current
    val vm: BudgetViewModel = viewModel()
    val isCumulativeBudget by tallyBudgetService.isCumulativeBudgetObservableDTO.collectAsState(
        initial = false
    )
    val defaultBudget by tallyBudgetService.monthlyDefaultBudgetObservableDTO.collectAsState(initial = null)
    val isSetDefaultMonthBudget by tallyBudgetService.isSetMonthlyDefaultBudgetObservableDTO.collectAsState(
        initial = false
    )
    val monthDateTime: Long? by vm.monthDateTimeObservableDTO.collectAsState(initial = null)
    val budgetVO: MonthBudgetBoardVO? by vm.currentMonthBudgetObservableVO.collectAsState(initial = null)
    val isSetMonthBudget by vm.isSetMonthBudgetObservableDTO.collectAsState(initial = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nothing(),
    ) {

        Spacer(modifier = Modifier.height(height = 8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .nothing(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .nothing(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_budget_cumulative),
                    style = MaterialTheme.typography.subtitle1,
                )
                Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                Switch(
                    checked = isCumulativeBudget,
                    onCheckedChange = {
                        vm.setCumulativeBudget(context = context, value = it)
                    },
                )
            }
            Spacer(modifier = Modifier.height(height = 0.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.6f)
                    .nothing(),
                text = stringResource(id = R.string.res_str_desc9),
                style = MaterialTheme.typography.body2,
            )
        }

        Spacer(modifier = Modifier.height(height = 4.dp))

        Divider(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .nothing(),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    vm.toFillDefaultMonthBudget(context = context)
                }
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .nothing(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.res_str_monthly_default_budget),
                style = MaterialTheme.typography.subtitle1,
            )
            Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
            Text(
                text = defaultBudget?.tallyCostAdapter()?.tallyNumberFormat1()?: stringResource(id = R.string.res_str_not_set),
                style = MaterialTheme.typography.body1,
            )
            Image(
                modifier = Modifier
                    .size(size = 18.dp)
                    .nothing(),
                painter = painterResource(id = R.drawable.res_arrow_right1),
                contentDescription = null
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .nothing(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Red,
                contentColor = Color.White,
            ),
            onClick = {
                vm.deleteDefaultBudget()
            },
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 6.dp)
                    .nothing(),
                text = stringResource(id = R.string.res_str_delete_default_budget),
                style = MaterialTheme.typography.body2,
            )
        }

        Spacer(modifier = Modifier.height(height = 6.dp))

        Divider(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .nothing(),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    vm.selectMonthTime(context = context)
                }
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .nothing(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (monthDateTime == null) "---" else SimpleDateFormat(
                    "yyyy-MM",
                    Locale.getDefault()
                ).format(
                    Date(
                        monthDateTime!!
                    )
                ),
                style = MaterialTheme.typography.subtitle1,
            )
            Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
            Image(
                modifier = Modifier
                    .size(size = 18.dp)
                    .nothing(),
                painter = painterResource(id = R.drawable.res_arrow_right1),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.height(height = 8.dp))

        BudgetBoardView(vo = budgetVO)

        Spacer(modifier = Modifier.height(height = 2.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .nothing(),
            onClick = {
                vm.toFillMonthBudget(context = context)
            },
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 6.dp)
                    .nothing(),
                text = stringResource(id = if (isSetMonthBudget) R.string.res_str_edit_budget else R.string.res_str_set_budget),
                style = MaterialTheme.typography.body2,
            )
        }

        if (isSetMonthBudget) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .nothing(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red,
                    contentColor = Color.White,
                ),
                onClick = {
                    vm.deleteMonthBudget()
                },
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 6.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_delete_budget),
                    style = MaterialTheme.typography.body2,
                )
            }
        }

    }
}

@Composable
fun BudgetViewWrap() {
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_budget_manage,
            )
        }
    ) {
        BudgetView()
    }
}

@Preview
@Composable
private fun BudgetViewPreview() {
    BudgetView()
}