package com.xiaojinzi.tally.bill.module.cate_group_bill_list.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.view.BillPageListView

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun CateGroupBillViewWrap() {
    val vm: CateGroupBillViewModel = viewModel()
    val targetCateGroup by vm.cateGroupObservableDTO.collectAsState(initial = null)
    val titleName = targetCateGroup?.run {
        this.name?: stringResource(id = this.nameRsd!!)
    }?: ""
    Scaffold(
        topBar = {
            AppbarNormal(
                title = titleName,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nothing(),
        ) {
            val billGroupVOList = vm.currBillPageListObservableVO.collectAsLazyPagingItems()
            BillPageListView(
                modifier = Modifier.weight(weight = 1f, fill = true),
                vos = billGroupVOList
            )
        }
    }
}