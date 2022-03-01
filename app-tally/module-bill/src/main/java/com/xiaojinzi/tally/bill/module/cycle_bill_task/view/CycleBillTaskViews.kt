package com.xiaojinzi.tally.bill.module.cycle_bill_task.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.TallyBillUsageDTO
import com.xiaojinzi.tally.base.view.CycleTaskItem
import com.xiaojinzi.tally.bill.R

@Composable
private fun CycleBillTaskView() {
    val context = LocalContext.current
    val vm: CycleBillTaskViewModel = viewModel()
    val taskList by vm.allBillTaskObservableDTO.collectAsState(initial = emptyList())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.surface)
            .nothing(),
    ) {

            taskList.forEachIndexed { index, item ->
                if (index > 0) {
                    Divider()
                }
                CycleTaskItem(
                    modifier = Modifier
                        .clickable {
                            vm.selectItem(context = context, index = index)
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .nothing(),
                    billDetail = item,
                )

        }

    }
}

@Composable
fun CycleBillTaskViewWrap() {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_cycle_bill_task,
                menu1IconRsd = R.drawable.res_add4,
                menu1ClickListener = {
                    Router.with(context)
                        .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
                        .putSerializable("usage", TallyBillUsageDTO.CycleTask)
                        .forward()
                },
            )
        }
    ) {
        CycleBillTaskView()
    }
}

@Preview
@Composable
private fun CycleBillTaskViewPreview() {
    CycleBillTaskView()
}