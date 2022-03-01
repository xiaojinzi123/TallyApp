package com.xiaojinzi.tally.bill.module.sub_reimbursement_bill_list.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.view.BillPageListView
import com.xiaojinzi.tally.bill.R

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun SubReimbursementBillListView() {
    val vm: SubReimbursementBillListViewModel = viewModel()
    val billListVO = vm.currBillPageListObservableVO.collectAsLazyPagingItems()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nothing(),
    ) {
        BillPageListView(vos = billListVO)
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun SubReimbursementBillListViewWrap() {
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_sub_reimbursement_bill
            )
        }
    ) {
        SubReimbursementBillListView()
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Preview
@Composable
private fun SubReimbursementBillListViewPreview() {
    SubReimbursementBillListView()
}