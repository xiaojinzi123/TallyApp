package com.xiaojinzi.tally.bill.module.bill_auto.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.flow.toggle
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.support.settingService
import com.xiaojinzi.tally.base.view.CommonActionItemView
import com.xiaojinzi.tally.base.view.CommonSurface1
import com.xiaojinzi.tally.base.view.CommonSwitchItemView
import com.xiaojinzi.tally.bill.R

@Composable
private fun BillAutoView() {
    val context = LocalContext.current
    val vm: BillAutoViewModel = viewModel()
    val isOpenAutoBillFeature by vm
        .isOpenAutoBillFeatureObservableDTO
        .collectAsState(initial = false)
    val isTipToOpenAutoBill by settingService.isTipToOpenAutoBillObservableDTO
        .collectAsState(initial = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .nothing(),
    ) {

        CommonSurface1 {

            CommonActionItemView(
                contentNameItem = R.string.res_str_auto_bill_course.toStringItemDTO()
            ) {
                Router.with(context)
                    .url("https://xiaojinzi.notion.site/f686fbec969c40c3b15e242181578000")
                    .forward()
            }

        }

        Spacer(modifier = Modifier.height(height = 16.dp))

        CommonSurface1 {
            CommonSwitchItemView(
                contentItem = R.string.res_str_open_auto_bill.toStringItemDTO(),
                descItem = R.string.res_str_desc3.toStringItemDTO(),
                checked = isOpenAutoBillFeature
            ) {
                vm.openAutoBillFeature(context = context)
            }

            CommonSwitchItemView(
                contentItem = R.string.res_str_open_auto_bill_tip.toStringItemDTO(),
                descItem = R.string.res_str_desc4.toStringItemDTO(),
                checked = isTipToOpenAutoBill
            ) {
                settingService.isTipToOpenAutoBillObservableDTO.toggle()
            }
        }

        Spacer(modifier = Modifier.height(height = 16.dp))

        CommonSurface1 {

            CommonActionItemView(
                contentNameItem = R.string.res_str_set_default_account.toStringItemDTO()
            ) {
                Router.with(context)
                    .hostAndPath(TallyRouterConfig.TALLY_BILL_AUTO_DEFAULT_ACCOUNT)
                    .forward()
            }

            CommonActionItemView(
                contentNameItem = R.string.res_str_set_default_category.toStringItemDTO()
            ) {
                Router.with(context)
                    .hostAndPath(TallyRouterConfig.TALLY_BILL_AUTO_DEFAULT_CATEGORY)
                    .forward()
            }

        }

        Spacer(modifier = Modifier.height(height = 16.dp))

        val pageName = listOf(
            "支付宝详情页",
            "支付宝支付成功页",
            "支付宝转账成功页",
            "微信详情页",
            "微信支付成功页",
            "云闪付-我的-账单详情页",
            "云闪付-消息-账单详情页",
            "云闪付-消息-交易详情页",
            "云闪付支付成功页",
            "云闪付转账成功页",
        )

        CommonSurface1 {

            Text(
                text = stringResource(id = R.string.res_str_support_view),
                style = MaterialTheme.typography.subtitle1,
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            pageName.forEachIndexed { index, item ->
                Text(
                    modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 4.dp)
                        .nothing(),
                    text = "${index + 1}. $item",
                    style = MaterialTheme.typography.body1,
                )
            }

        }

    }
}

@Composable
fun BillAutoViewWrap() {
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_auto_bill,
            )
        }
    ) {
        BillAutoView()
    }
}

@Preview
@Composable
private fun BillAutoViewPreview() {
    BillAutoView()
}