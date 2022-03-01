package com.xiaojinzi.tally.account.module.detail.view

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.navigationBarsPadding
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.bean.INVALID_STRING
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.flow.toggle
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.account.R
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.base.view.BillPageListView
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
private fun AccountDetailView(
    vo: AccountDetailVO?
) {
    val vm: AccountDetailViewModel = viewModel()
    val billListVO = vm.currBillPageListObservableVO.collectAsLazyPagingItems()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .nothing(),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.small)
                .background(color = MaterialTheme.colors.primary)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .nothing(),
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.res_str_initial_balance),
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
                Spacer(modifier = Modifier.height(height = 10.dp))
                Text(
                    text = (vo?.initialBalance ?: 0f).tallyNumberFormat1(),
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
            }
            Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
            Column {
                Text(
                    text = stringResource(id = R.string.res_str_account_balance),
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
                Spacer(modifier = Modifier.height(height = 10.dp))
                Text(
                    text = (vo?.balance ?: 0f).tallyNumberFormat1(),
                    style = MaterialTheme.typography.subtitle1.copy(
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
                .background(color = MaterialTheme.colors.surface)
                .padding(horizontal = 0.dp, vertical = 0.dp)
                .nothing(),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Row(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .wrapContentHeight()
                    .clickable {
                        vm.toCreateBill(context = context, isTransfer = false)
                    }
                    .padding(horizontal = 0.dp, vertical = 10.dp)
                    .nothing(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .size(size = 20.dp),
                    painter = painterResource(id = R.drawable.res_add5),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(width = 10.dp))
                Text(
                    text = stringResource(id = R.string.res_str_create_a_bill),
                    style = MaterialTheme.typography.body2,
                )
            }

            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .height(height = 24.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.Gray)
                    .nothing(),
            )

            Row(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .wrapContentHeight()
                    .clickable {
                        vm.toCreateBill(context = context, isTransfer = true)
                    }
                    .padding(horizontal = 0.dp, vertical = 10.dp)
                    .nothing(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .size(size = 20.dp),
                    painter = painterResource(id = R.drawable.res_transfer1),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(width = 10.dp))
                Text(
                    text = stringResource(id = R.string.res_str_transfer),
                    style = MaterialTheme.typography.body2,
                )
            }

        }
        Spacer(modifier = Modifier.height(height = 10.dp))
        BillPageListView(
            vos = billListVO,
            isItemsRound = true,
            dayTitleHorizontalPadding = 0.dp,
            onEmptyClick = {
                vm.toCreateBill(context = context, isTransfer = false)
            }
        )

    }

}

@SuppressLint("CoroutineCreationDuringComposition")
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun AccountDetailViewWrap() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val vm: AccountDetailViewModel = viewModel()
    val isShowMenu by vm.isShowMenuVO.collectAsState(initial = false)
    val accountVO by vm.accountDetailVO.collectAsState(initial = null)
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    if (isShowMenu != sheetState.isVisible) {
        scope.launch {
            if (isShowMenu) {
                sheetState.show()
            } else {
                sheetState.hide()
            }
        }
    }
    LaunchedEffect(key1 = sheetState) {
        snapshotFlow { sheetState.isVisible }
            .collect {
                if (vm.isShowMenuVO.value != it) {
                    vm.isShowMenuVO.value = it
                }
            }
    }
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .nothing(),
            ) {
                if (accountVO?.isDefault == false) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                vm.isShowMenuVO.value = false
                                vm.setAsDefault()
                            }
                            .padding(horizontal = 0.dp, vertical = 18.dp)
                            .nothing(),
                        text = stringResource(id = R.string.res_str_set_as_default),
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                    )
                    Divider()
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.isShowMenuVO.value = false
                            Router.with(context)
                                .hostAndPath(TallyRouterConfig.TALLY_ACCOUNT_CREATE)
                                .apply {
                                    accountVO?.let {
                                        this.putString("accountId", it.accountId)
                                    }
                                }
                                .forward()
                        }
                        .padding(horizontal = 0.dp, vertical = 18.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_edit_account),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                )
                Divider()
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.isShowMenuVO.value = false
                            vm.deleteAccount(context = context)
                        }
                        .padding(horizontal = 0.dp, vertical = 18.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_delete_account),
                    style = MaterialTheme.typography.body1.copy(
                        color = Color.Red
                    ),
                    textAlign = TextAlign.Center,
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppbarNormal(
                    title = (accountVO?.accountName?.nameAdapter() ?: "") +
                            if (accountVO?.isDefault == true) {
                                "(${stringResource(id = R.string.res_str_default)})"
                            } else {
                                ""
                            },
                    menu1IconRsd = R.drawable.res_more1,
                    menu1ClickListener = {
                        scope.launch {
                            vm.isShowMenuVO.toggle()
                            /*if (sheetState.isVisible) {
                                sheetState.hide()
                            } else {
                                sheetState.show()
                            }*/
                        }
                    }
                )
            }
        ) {
            AccountDetailView(vo = accountVO)
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Preview
@Composable
private fun AccountDetailViewPreview() {
    AccountDetailView(
        AccountDetailVO(
            isDefault = false,
            accountId = INVALID_STRING,
            accountName = StringItemDTO(
                name = "测试",
            ),
            initialBalance = 100f,
            balance = 30f,
        )
    )
}