package com.xiaojinzi.tally.statistical.module.account.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.bean.INVALID_STRING
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.base.theme.body3
import com.xiaojinzi.tally.statistical.R

@Composable
private fun AccountItemView(vo: AccountStatisticalItemVO) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .clickable {
                Router.with(context)
                    .hostAndPath(TallyRouterConfig.TALLY_ACCOUNT_DETAIL)
                    .putString("accountId", vo.accountId)
                    .forward()
            }
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .nothing(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(size = 20.dp),
            painter = painterResource(id = vo.iconRsd),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(width = 10.dp))
        Text(
            text = vo.getNameAdapter(),
            style = MaterialTheme.typography.body2,
        )
        if (vo.isDefault) {
            Spacer(modifier = Modifier.width(width = 4.dp))
            Box(
                modifier = Modifier
                    .size(size = 4.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.Black)
                    .nothing(),
            )
            Spacer(modifier = Modifier.width(width = 4.dp))
            Text(
                text = stringResource(id = R.string.res_str_default),
                style = MaterialTheme.typography.body2,
            )
            Spacer(modifier = Modifier.width(width = 4.dp))
        }
        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
        Text(
            text = vo.balance.tallyCostAdapter().tallyNumberFormat1(),
            style = MaterialTheme.typography.body2,
        )
    }

}

@Composable
private fun AccountGroupView(vo: AccountStatisticalGroupVO) {
    Column(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.small)
            .background(color = MaterialTheme.colors.surface)
            .padding(vertical = 2.dp)
            .nothing(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .nothing(),
        ) {
            Text(
                text = vo.getNameAdapter(),
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
            Text(
                text = vo.balance.tallyCostAdapter().tallyNumberFormat1(),
                style = MaterialTheme.typography.body3.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
        vo.items.forEach { item ->
            AccountItemView(vo = item)
        }
    }

}

@Composable
fun AccountStatisticalOverviewForHomeView(
    vo: AccountStatisticalOverviewVO?,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // .fillMaxHeight()
            .height(intrinsicSize = IntrinsicSize.Max)
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                onClick.invoke()
            }
            .background(color = MaterialTheme.colors.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .nothing(),
    ) {
        Row(
            modifier = Modifier
                // .fillMaxHeight()
                .wrapContentHeight()
                .nothing(),
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .wrapContentHeight()
                    .nothing(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_total_asset),
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
                Spacer(modifier = Modifier.height(height = 10.dp))
                // 总资产的字符串
                val totalAssetStr = vo.run {
                    this?.totalAsset?.tallyCostAdapter()?.tallyNumberFormat1() ?: "---"
                }
                Text(
                    text = totalAssetStr,
                    style = MaterialTheme.typography.body2.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
            }
            Column(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .wrapContentHeight()
                    .nothing(),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_debt_asset),
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                    textAlign = TextAlign.End,
                )
                Spacer(modifier = Modifier.height(height = 10.dp))
                // 负资产的字符串
                val debtAssetStr = vo.run {
                    this?.debtAsset?.tallyCostAdapter()?.tallyNumberFormat1() ?: "---"
                }
                Text(
                    text = debtAssetStr,
                    style = MaterialTheme.typography.body2.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.height(height = 16.dp))
        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .nothing(),
        ) {
            Text(
                text = stringResource(id = R.string.res_str_net_asset),
                style = MaterialTheme.typography.subtitle1.copy(
                    color = MaterialTheme.colors.onPrimary
                ),
            )
            Spacer(modifier = Modifier.height(height = 10.dp))
            // 净资产的字符串
            val netAssetStr = vo.run {
                this?.netAsset?.tallyCostAdapter()?.tallyNumberFormat1() ?: "---"
            }
            Text(
                text = netAssetStr,
                style = MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.onPrimary
                ),
            )
        }
    }
}

@Composable
fun AccountStatisticalOverviewForMyView(
    vo: AccountStatisticalOverviewVO?,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = MaterialTheme.shapes.small)
            .clickable {
                onClick.invoke()
            }
            .background(color = MaterialTheme.colors.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .nothing(),
    ) {
        Column(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.res_str_net_asset),
                style = MaterialTheme.typography.subtitle2.copy(
                    color = MaterialTheme.colors.onPrimary
                ),
            )
            Spacer(modifier = Modifier.height(height = 10.dp))
            // 净资产的字符串
            val netAssetStr = vo.run {
                this?.netAsset?.tallyCostAdapter()?.tallyNumberFormat1() ?: "---"
            }
            Text(
                text = netAssetStr,
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onPrimary
                ),
            )
        }
        Column(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.res_str_total_asset),
                style = MaterialTheme.typography.subtitle2.copy(
                    color = MaterialTheme.colors.onPrimary
                ),
            )
            Spacer(modifier = Modifier.height(height = 10.dp))
            // 总资产的字符串
            val totalAssetStr = vo.run {
                this?.totalAsset?.tallyCostAdapter()?.tallyNumberFormat1() ?: "---"
            }
            Text(
                text = totalAssetStr,
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.onPrimary
                ),
            )
        }
        Column(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.res_str_debt_asset),
                style = MaterialTheme.typography.subtitle2.copy(
                    color = MaterialTheme.colors.onPrimary
                ),
                textAlign = TextAlign.End,
            )
            Spacer(modifier = Modifier.height(height = 10.dp))
            // 负资产的字符串
            val debtAssetStr = vo.run {
                this?.debtAsset?.tallyCostAdapter()?.tallyNumberFormat1() ?: "---"
            }
            Text(
                text = debtAssetStr,
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.onPrimary
                ),
            )
        }
    }
}

@Composable
fun AccountStatisticalOverviewViewWrap(
    type: AccountStatisticalOverviewType,
    clickJumpToAccountView: Boolean
) {
    val context = LocalContext.current
    val jumpToAccountView: () -> Unit = {
        if (clickJumpToAccountView) {
            Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_ACCOUNT_MAIN)
                .forward()
        }
    }
    val vm: AccountStatisticalViewModel = viewModel()
    val overviewVO by vm.accountStatisticalOverviewObservableVO.collectAsState(initial = null)
    when (type) {
        AccountStatisticalOverviewType.Home -> AccountStatisticalOverviewForHomeView(
            vo = overviewVO,
            onClick = jumpToAccountView
        )
        AccountStatisticalOverviewType.My -> AccountStatisticalOverviewForMyView(
            vo = overviewVO,
            onClick = jumpToAccountView
        )
    }
}

@Composable
fun AccountStatisticalView() {
    val vm: AccountStatisticalViewModel = viewModel()
    val groupItemList by vm.accountGroupListObservableVO.collectAsState(initial = emptyList())
    Column(
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .nothing(),
    ) {
        groupItemList.forEach { groupItem ->
            AccountGroupView(vo = groupItem)
            Spacer(modifier = Modifier.height(height = 12.dp))
        }
        Spacer(modifier = Modifier.height(height = 20.dp))
    }
}

@Preview
@Composable
private fun AccountStatisticalOverviewViewPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Max)
            .nothing(),
    ) {
        AccountStatisticalOverviewForHomeView(
            vo = AccountStatisticalOverviewVO(
                totalAsset = 100000,
                debtAsset = 33556,
            )
        )
        Spacer(modifier = Modifier.height(height = 12.dp))
        AccountStatisticalOverviewForMyView(
            vo = AccountStatisticalOverviewVO(
                totalAsset = 100000,
                debtAsset = 33556,
            )
        )
    }
}

@Preview
@Composable
private fun AccountItemViewPreview() {
    AccountItemView(
        vo = AccountStatisticalItemVO(
            isDefault = false,
            accountId = INVALID_STRING,
            iconRsd = R.drawable.res_add1,
            name = "测试账户1",
            balance = 10000,
        )
    )
}

@Preview
@Composable
private fun AccountGroupViewPreview() {
    AccountGroupView(
        vo = AccountStatisticalGroupVO(
            name = "测试账户组",
            balance = 20000,
            items = listOf(
                AccountStatisticalItemVO(
                    isDefault = false,
                    accountId = INVALID_STRING,
                    iconRsd = R.drawable.res_add1,
                    name = "测试账户1",
                    balance = 10000,
                ),
                AccountStatisticalItemVO(
                    isDefault = false,
                    accountId = INVALID_STRING,
                    iconRsd = R.drawable.res_add2,
                    name = "测试账户2",
                    balance = 10000,
                ),
            )
        )
    )
}