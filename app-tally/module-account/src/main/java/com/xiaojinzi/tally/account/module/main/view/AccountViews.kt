package com.xiaojinzi.tally.account.module.main.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.account.R
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.support.statisticalService

@Composable
fun AccountView() {

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .nothing(),
    ) {
        Spacer(modifier = Modifier.height(height = 16.dp))
        statisticalService.accountStatisticalOverviewViewForMy(clickJumpToAccountView = false)
        Spacer(modifier = Modifier.height(height = 16.dp))
        statisticalService.accountStatisticalView()
    }

}

@Composable
fun AccountViewWrap() {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_my_account,
                menu1IconRsd = R.drawable.res_add4,
                menu1ClickListener = {
                    Router.with(context)
                        .hostAndPath(TallyRouterConfig.TALLY_ACCOUNT_CREATE)
                        .forward()
                }
            )
        }
    ) {
        AccountView()
    }
}

@Preview
@Composable
fun AccountViewPreview() {
    AccountView()
}