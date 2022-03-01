package com.xiaojinzi.tally.account.module.select.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.bean.INVALID_STRING
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.view.compose.BottomView
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.account.R
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyNumberFormat1

@Composable
fun AccountSelectItemView(vo: AccountSelectItemVO) {
    val vm: AccountSelectViewModel = viewModel()
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                vm.userSelectIdObservable.value = vo.uid
                vm.returnData(context = context)
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .nothing(),
    ) {
        Image(
            modifier = Modifier
                .size(size = 18.dp),
            painter = painterResource(id = vo.iconRsd),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(width = 14.dp))
        Text(
            text = vo.name.nameAdapter(),
            style = MaterialTheme.typography.body2,
        )
        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
        Text(
            text = vo.balance.tallyCostAdapter().tallyNumberFormat1(),
            style = MaterialTheme.typography.body2,
        )
    }
}

@Composable
fun AccountSelectView(vos: List<AccountSelectItemVO>) {
    val context = LocalContext.current
    BottomView {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(
                    shape = RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp
                    )
                )
                .background(color = MaterialTheme.colors.background)
                .padding(horizontal = 0.dp, vertical = 16.dp)
                .nothing(),
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 0.dp)
                    .nothing(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_select_account),
                    style = MaterialTheme.typography.subtitle1.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                IconButton(
                    onClick = {
                        Router.with(context)
                            .hostAndPath(TallyRouterConfig.TALLY_ACCOUNT_CREATE)
                            .forward()
                    }
                ) {
                    Image(
                        modifier = Modifier
                            .size(size = 24.dp)
                            .nothing(),
                        painter = painterResource(id = R.drawable.res_add3),
                        contentDescription = null
                    )
                }
            }
            Column(
                modifier = Modifier
                    .verticalScroll(state = rememberScrollState())
                    .nothing(),
            ) {
                vos.forEach { item ->
                    AccountSelectItemView(vo = item)
                }
            }
        }
    }
}

@Composable
fun AccountSelectViewWrap() {
    val vm: AccountSelectViewModel = viewModel()
    val accountList by vm.accountListObservableVO.collectAsState(initial = emptyList())
    AccountSelectView(vos = accountList)
}

@Preview
@Composable
private fun AccountSelectViewPreview1() {
    AccountSelectView(
        vos = (1..3).map {
            AccountSelectItemVO(
                uid = INVALID_STRING,
                isSelect = it == 1,
                iconRsd = R.drawable.res_bottle1,
                name = StringItemDTO(
                    name = "基金$it"
                ),
            )
        }
    )
}

@Preview
@Composable
private fun AccountSelectViewPreview2() {
    AccountSelectView(
        vos = (1..20).map {
            AccountSelectItemVO(
                uid = INVALID_STRING,
                isSelect = it == 10,
                iconRsd = R.drawable.res_bottle1,
                name = StringItemDTO(
                    name = "基金$it"
                ),
            )
        }
    )
}