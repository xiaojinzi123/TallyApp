package com.xiaojinzi.tally.bill.module.cycle_bill_create.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.commonTimeFormat3
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.view.CommonActionItemView
import com.xiaojinzi.tally.base.view.CycleTaskItem
import com.xiaojinzi.tally.bill.R

@Composable
private fun CycleBillCreateView() {
    val context = LocalContext.current
    val vm: CycleBillCreateViewModel = viewModel()
    val cycleBillTask by vm.cycleBillTaskObservableDTO.collectAsState(initial = null)
    val startTime by vm.startTimeObservableDTO.collectAsState(initial = null)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nothing(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .nothing(),
        ) {
            Text(
                text = stringResource(id = R.string.res_str_cycle_bill_task),
                style = MaterialTheme.typography.body1,
            )
            if (cycleBillTask == null) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.toChooseBillTask(context = context)
                        }
                        .padding(horizontal = 0.dp, vertical = 18.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_tip10),
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.primary,
                    ),
                    textAlign = TextAlign.Center,
                )
            } else {
                Spacer(modifier = Modifier.height(height = 10.dp))
                CycleTaskItem(
                    modifier = Modifier
                        .clickable {
                            vm.toChooseBillTask(context = context)
                        }
                        .nothing(),
                    billDetail = cycleBillTask!!,
                )
            }
        }

        Spacer(modifier = Modifier.height(height = 16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .nothing(),
        ) {

            Spacer(modifier = Modifier.height(height = 10.dp))

            Text(
                text = stringResource(id = R.string.res_str_cycle_set),
                style = MaterialTheme.typography.body1,
            )

            CommonActionItemView(
                contentNameItem = "首次入账日期".toStringItemDTO(),
                contentValueItem = startTime?.commonTimeFormat3()?.toStringItemDTO()
                    ?: R.string.res_str_not_set.toStringItemDTO(),
            ) {
                vm.toChooseStartTime(context = context)
            }

            Divider()

            CommonActionItemView(
                contentNameItem = "重复频率".toStringItemDTO(),
                contentValueItem = "qwe".toStringItemDTO(),
            ) {

            }

            Divider()

            CommonActionItemView(
                contentNameItem = "结束条件".toStringItemDTO(),
                contentValueItem = "qwe".toStringItemDTO(),
            ) {

            }

        }

        Spacer(modifier = Modifier.height(height = 16.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .clip(shape = CircleShape)
                .background(color = MaterialTheme.colors.primary)
                .padding(horizontal = 0.dp, vertical = 12.dp)
                .nothing(),
            text = stringResource(id = R.string.res_str_create),
            style = MaterialTheme.typography.subtitle1.copy(
                color = Color.White,
            ),
            textAlign = TextAlign.Center,
        )


    }
}

@Composable
fun CycleBillCreateViewWrap() {
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_new_cycle_bill
            )
        }
    ) {
        CycleBillCreateView()
    }
}

@Preview
@Composable
private fun CycleBillCreateViewPreview() {
    CycleBillCreateView()
}