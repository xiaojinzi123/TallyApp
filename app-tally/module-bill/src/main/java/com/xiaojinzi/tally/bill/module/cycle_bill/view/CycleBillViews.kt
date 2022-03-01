package com.xiaojinzi.tally.bill.module.cycle_bill.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.bill.R

@Composable
private fun CycleBillView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nothing(),
    ) {
        // TODO
    }
}

@Composable
fun CycleBillViewWrap() {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_cycle_bill
            )
        },
        floatingActionButton = {
            IconButton(onClick = {
                Router
                    .with(context)
                    .hostAndPath(TallyRouterConfig.TALLY_CYCLE_BILL_CREATE)
                    .forward()
            }) {
                Image(
                    modifier = Modifier
                        .size(size = 42.dp)
                        .nothing(),
                    painter = painterResource(id = R.drawable.res_add3),
                    contentDescription = null
                )
            }
        }
    ) {
        CycleBillView()
    }
}

@Preview
@Composable
private fun CycleBillViewPreview() {
    CycleBillView()
}