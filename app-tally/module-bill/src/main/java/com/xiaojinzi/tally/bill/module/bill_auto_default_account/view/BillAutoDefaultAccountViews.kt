package com.xiaojinzi.tally.bill.module.bill_auto_default_account.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.view.CommonActionItemView
import com.xiaojinzi.tally.bill.R

@Composable
private fun BillAutoDefaultAccountView() {
    val context = LocalContext.current
    val vm: BillAutoDefaultAccountViewModel = viewModel()
    val vos by vm.dataListObservableVO.collectAsState(initial = emptyList())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .nothing(),
    ) {
        vos.forEachIndexed { index, itemVO ->
            if (index > 0) {
                Divider()
            }
            CommonActionItemView(
                contentNameItem = itemVO.name,
                imgRsd = itemVO.accountIcon,
                contentValueItem = itemVO.accountName,
            ) {
                vm.selectAccount(
                    context = context,
                    uid = itemVO.uid,
                )
            }
        }
    }
}

@Composable
fun BillAutoDefaultAccountViewWrap() {
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_set_default_account
            )
        }
    ) {
        BillAutoDefaultAccountView()
    }
}

@Preview
@Composable
private fun BillAutoDefaultAccountViewPreview() {
    BillAutoDefaultAccountView()
}