package com.xiaojinzi.tally.my.module.main.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.view.compose.GridView
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.support.statisticalService
import com.xiaojinzi.tally.base.support.tallyAppService
import com.xiaojinzi.tally.my.R

private val functionList1 = listOf(
    MyFunctionVO(
        iconRsd = R.drawable.res_setting1,
        name = StringItemDTO(
            nameRsd = R.string.res_str_setting,
        ),
        action = {
            Router
                .with(it)
                .hostAndPath(TallyRouterConfig.TALLY_SETTING)
                .forward()
        }
    ),
    MyFunctionVO(
        iconRsd = R.drawable.res_calendar1,
        name = StringItemDTO(
            nameRsd = R.string.res_str_cycle_bill,
        ),
        action = {
            Router
                .with(it)
                .hostAndPath(TallyRouterConfig.TALLY_CYCLE_BILL)
                .forward()
        }
    ),
)

private val functionList2 = if (tallyAppService.autoBillEnable) {
    listOf(
        MyFunctionVO(
            iconRsd = R.drawable.res_auto1,
            name = StringItemDTO(
                nameRsd = R.string.res_str_auto_bill,
            ),
            action = {
                Router
                    .with(it)
                    .hostAndPath(TallyRouterConfig.TALLY_BILL_AUTO)
                    .forward()
            }
        ),
    )
} else {
    emptyList()
}

private val functionList3 = listOf(
    MyFunctionVO(
        iconRsd = R.drawable.res_money1,
        name = StringItemDTO(
            nameRsd = R.string.res_str_my_account,
        ),
        action = {
            Router
                .with(it)
                .hostAndPath(TallyRouterConfig.TALLY_ACCOUNT_MAIN)
                .forward()
        }
    ),
    MyFunctionVO(
        iconRsd = R.drawable.res_label1,
        name = StringItemDTO(
            nameRsd = R.string.res_str_label_management,
        ),
        action = {
            Router
                .with(it)
                .hostAndPath(TallyRouterConfig.TALLY_LABEL_LIST)
                .forward()
        }
    ),
    MyFunctionVO(
        iconRsd = R.drawable.res_book2,
        name = StringItemDTO(
            nameRsd = R.string.res_str_books_management,
        ),
        action = {
            Router
                .with(it)
                .hostAndPath(TallyRouterConfig.TALLY_BILL_BOOK)
                .forward()
        }
    ),
    MyFunctionVO(
        iconRsd = R.drawable.res_category1,
        name = StringItemDTO(
            nameRsd = R.string.res_str_classified_management,
        ),
        action = {
            Router
                .with(it)
                .hostAndPath(TallyRouterConfig.TALLY_CATEGORY)
                .forward()
        }
    ),
    MyFunctionVO(
        iconRsd = R.drawable.res_budget1,
        name = StringItemDTO(
            nameRsd = R.string.res_str_budget,
        ),
        action = {
            Router
                .with(it)
                .hostAndPath(TallyRouterConfig.TALLY_BILL_BUDGET)
                .forward()
        }
    ),
    MyFunctionVO(
        iconRsd = R.drawable.res_star1,
        name = StringItemDTO(
            nameRsd = R.string.res_str_evaluation,
        ),
        action = {
            Router
                .with(it)
                .hostAndPath(RouterConfig.SYSTEM_APP_MARKET)
                .putString("packageName", tallyAppService.appChannel.packageName)
                .forward()
        }
    ),
)

private val functionList = functionList1 + functionList2 + functionList3

@Composable
private fun FunctionView(
    @DrawableRes
    iconRsd: Int,
    name: StringItemDTO,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = iconRsd),
            contentDescription = null,
            modifier = Modifier.size(size = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = name.nameAdapter(),
            style = MaterialTheme.typography.subtitle2,
            textAlign = TextAlign.Center,
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun MyView() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .nothing(),
    ) {
        Surface(
            color = MaterialTheme.colors.primary,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .statusBarsPadding()
                .padding(top = 20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 16.dp)
                    .nothing(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.res_tally_app_name),
                    style = MaterialTheme.typography.subtitle1,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.res_str_happy_every_day),
                    style = MaterialTheme.typography.subtitle1,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        statisticalService.accountStatisticalOverviewViewForMy(clickJumpToAccountView = true)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = MaterialTheme.shapes.small,
                )
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .nothing(),
        ) {
            GridView(items = functionList, columnNumber = 4) { vo ->
                FunctionView(
                    iconRsd = vo.iconRsd,
                    name = vo.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable {
                            vo.action.invoke(context)
                        }
                        .padding(horizontal = 0.dp, vertical = 8.dp)
                        .nothing()
                )
            }
        }
        Spacer(modifier = Modifier.height(46.dp))
        /*val testList: LazyPagingItems<BillDetailDayDTO> =
            tallyBillDetailQueryPagingService
                .subscribePageBillDetailObservable()
                .collectAsLazyPagingItems()
        LazyColumn {
            items(items = testList) {
                it ?: return@items
                Column {
                    Text(
                        text = "cateGroup = ${
                            SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            ).format(Date(it.dayStartTime))
                        }"
                    )
                }
            }
            testList.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item {
                            Text(text = "我正在刷新")
                        }
                    }
                    loadState.append is LoadState.Loading -> {
                        item {
                            Text(text = "我正在加载")
                        }
                    }
                }
            }
        }*/
    }
}

@ExperimentalFoundationApi
@Composable
fun MyViewWrap() {
    MyView()
}

@ExperimentalFoundationApi
@Preview
@Composable
fun MyViewPreview() {
    MyView()
}