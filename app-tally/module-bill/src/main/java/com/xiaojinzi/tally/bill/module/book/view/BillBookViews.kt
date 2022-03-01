package com.xiaojinzi.tally.bill.module.book.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.bean.INVALID_STRING
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.bill.R

@Composable
private fun BillBookView(vos: List<BillBookVO>) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .navigationBarsPadding()
            .nothing(),
    ) {
        vos.forEachIndexed { index, vo ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(height = 8.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = MaterialTheme.shapes.small)
                    .clickable {
                        Router.with(context)
                            .hostAndPath(TallyRouterConfig.TALLY_BILL_BOOK_DETAIL)
                            .putString("bookId", vo.bookId)
                            .forward()
                    }
                    .background(
                        color = MaterialTheme.colors.surface,
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .nothing(),
            ) {
                Text(
                    text = vo.name.nameAdapter(),
                    style = MaterialTheme.typography.subtitle1,
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                Row {
                    Text(
                        text = stringResource(id = R.string.res_str_total_spending1),
                        style = MaterialTheme.typography.body2,
                    )
                    Spacer(modifier = Modifier.width(width = 2.dp))
                    Text(
                        text = vo.spending.tallyNumberFormat1(),
                        style = MaterialTheme.typography.body2,
                    )
                    Spacer(modifier = Modifier.width(width = 24.dp))
                    Text(
                        text = stringResource(id = R.string.res_str_total_income1),
                        style = MaterialTheme.typography.body2,
                    )
                    Spacer(modifier = Modifier.width(width = 2.dp))
                    Text(
                        text = vo.income.tallyNumberFormat1(),
                        style = MaterialTheme.typography.body2,
                    )
                }
                Spacer(modifier = Modifier.height(height = 8.dp))
                Text(
                    text = stringResource(
                        id = R.string.res_str_a_total_of_number_bills, vo.numberOfBill,
                    ),
                    style = MaterialTheme.typography.subtitle2,
                )
            }
        }
    }
}

@Composable
internal fun BillBookViewWrap() {
    val context = LocalContext.current
    val vm: BillBookViewModel = viewModel()
    val dataListVO by vm.dataListObservableVO.collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_books_management,
                menu1IconRsd = R.drawable.res_add4,
                menu1ClickListener = {
                    Router.with(context)
                        .hostAndPath(TallyRouterConfig.TALLY_BILL_BOOK_CREATE)
                        .forward()
                }
            )
        }
    ) {
        BillBookView(vos = dataListVO)
    }
}

@Preview
@Composable
private fun BillBookViewPreview() {
    BillBookView(
        vos = (1..5).map { num ->
            BillBookVO(
                bookId = INVALID_STRING,
                name = StringItemDTO(
                    name = "账本$num",
                ),
                spending = (1..1000).map { it.toFloat() }.random(),
                income = (1..1000).map { it.toFloat() }.random(),
                numberOfBill = (1..100).random(),
            )
        }
    )
}