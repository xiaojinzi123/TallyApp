package com.xiaojinzi.tally.bill.module.label_list.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.view.BillPageListView

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun CateGroupBillViewWrap() {
    val vm: LabelBillListViewModel = viewModel()
    Scaffold(
        topBar = {
            AppbarNormal(
                title = "",
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nothing(),
        ) {
            val targetBillList = vm.currBillPageListObservableVO.collectAsLazyPagingItems()
            BillPageListView(
                modifier = Modifier.weight(weight = 1f, fill = true),
                vos = targetBillList
            )
        }
    }
}