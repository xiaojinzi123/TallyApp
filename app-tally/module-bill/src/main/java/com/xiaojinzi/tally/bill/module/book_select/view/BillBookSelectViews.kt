package com.xiaojinzi.tally.bill.module.book_select.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.view.compose.BottomView
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.bill.R

@Composable
fun BillBookCheckAllItemView() {
    val vm: BillBookSelectViewModel = viewModel()
    val isAllSelect by vm.selectAllObservableVO.collectAsState(initial = false)
    Row(
        modifier = Modifier
            .clickable {
                vm.toggleAll()
            }
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colors.surface)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .nothing(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = R.string.res_str_select_ledger),
            style = MaterialTheme.typography.subtitle1,
        )
        Spacer(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .nothing(),
        )
        Text(
            modifier = Modifier
                .wrapContentSize()
                .nothing(),
            text = if (isAllSelect) {
                stringResource(id = R.string.res_str_unselect_all)
            } else {
                stringResource(id = R.string.res_str_select_all)
            },
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.width(width = 4.dp))
        Checkbox(
            checked = isAllSelect,
            onCheckedChange = {
                vm.toggleAll()
            },
        )

    }
}

@Composable
fun BillBookSelectItemView(item: BillBookItemVO) {
    val vm: BillBookSelectViewModel = viewModel()
    Row(
        modifier = Modifier
            .clickable {
                vm.toggleSelect(targetBookId = item.bookId)
            }
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .nothing(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 高度的最小值
        Spacer(modifier = Modifier.height(height = 48.dp))
        Text(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .nothing(),
            text = item.name ?: stringResource(id = item.nameRsd!!),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Start,
        )
        if (vm.isMultiSelect) {
            Checkbox(
                checked = item.isSelect,
                onCheckedChange = {
                    vm.toggleSelect(targetBookId = item.bookId)
                },
            )
        } else if (vm.isSingleSelect && item.isSelect) {
            Checkbox(
                checked = true,
                onCheckedChange = {
                    vm.toggleSelect(targetBookId = item.bookId)
                },
            )
        }
    }
}

@Composable
fun BillBookSelectView() {
    val context = LocalContext.current
    val vm: BillBookSelectViewModel = viewModel()
    val billBookList by vm.billBookListObservableVO.collectAsState(initial = emptyList())
    BottomView {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f, fill = true)
                .nothing()
        ) {
            Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
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
                    .background(color = MaterialTheme.colors.surface)
                    .nothing()
            ) {
                if (vm.isMultiSelect) {
                    BillBookCheckAllItemView()
                    Divider()
                } else {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 0.dp)
                            .padding(top = 4.dp)
                            .nothing(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = R.string.res_str_select_ledger),
                            style = MaterialTheme.typography.subtitle1,
                        )
                        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                        IconButton(
                            onClick = {
                                Router.with(context)
                                    .hostAndPath(TallyRouterConfig.TALLY_BILL_BOOK_CREATE)
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
                    Divider()
                }
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(bottom = 10.dp)
                        .verticalScroll(state = rememberScrollState()),
                ) {
                    billBookList.forEachIndexed { index, item ->
                        if (index > 0) {
                            Divider()
                        }
                        BillBookSelectItemView(item = item)
                    }
                }
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .nothing(),
            onClick = {
                vm.returnData(context = context)
            }
        ) {
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .navigationBarsPadding()
                    .padding(horizontal = 0.dp, vertical = 4.dp)
                    .nothing(),
                text = stringResource(id = R.string.res_str_done),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun BillBookSelectViewWrap() {
    BillBookSelectView()
}

@Preview
@Composable
fun BillBookSelectViewPreview() {
    BillBookSelectView()
}