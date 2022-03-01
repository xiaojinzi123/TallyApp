package com.xiaojinzi.tally.base.view

import androidx.annotation.Keep
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.R
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.support.tallyNumberFormat1

@Keep
data class BillMonthListReportItem(
    val timestamp: Long,
    val monthStr1: StringItemDTO,
    val spending: Float,
    val income: Float,
    val balance: Float = spending + income
)

@Composable
private fun BillMonthListReportViewTitleItem() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .nothing(),
    ) {
        Text(
            text = stringResource(id = R.string.res_str_month1),
            style = MaterialTheme.typography.subtitle1.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.Start,
        )
        Text(
            text = stringResource(id = R.string.res_str_spending),
            style = MaterialTheme.typography.subtitle1.copy(
                color = Color.Red,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = R.string.res_str_income),
            style = MaterialTheme.typography.subtitle1.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = R.string.res_str_balance),
            style = MaterialTheme.typography.subtitle1.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun BillMonthListReportViewItem(vo: BillMonthListReportItem) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                Router.with(context)
                    .hostAndPath(TallyRouterConfig.TALLY_BILL_MONTHLY)
                    .putLong(
                        "timestamp", vo.timestamp,
                    )
                    .forward()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .nothing(),
    ) {
        Text(
            text = vo.monthStr1.nameAdapter(),
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.Start,
        )
        Text(
            text = vo.spending.tallyNumberFormat1(),
            style = MaterialTheme.typography.body1.copy(
                color = Color.Red
            ),
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = vo.income.tallyNumberFormat1(),
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = vo.balance.tallyNumberFormat1(),
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
fun BillMonthListReportView(vos: List<BillMonthListReportItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .nothing(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (vos.isEmpty()) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.res_lottie_empty2)
            )
            LottieAnimation(
                composition = composition,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.4f)
                    .aspectRatio(ratio = 1f)
                    .nothing(),
                iterations = LottieConstants.IterateForever,
            )
        } else {
            BillMonthListReportViewTitleItem()
            vos.forEachIndexed { _, item ->
                BillMonthListReportViewItem(vo = item)
            }
        }
    }
}

@Preview
@Composable
private fun BillMonthListReportViewPreview() {
    BillMonthListReportView(
        vos = listOf(
            BillMonthListReportItem(
                timestamp = -1,
                monthStr1 = StringItemDTO(name = "10月"),
                spending = 345.23f,
                income = 123.33f
            ),
            BillMonthListReportItem(
                timestamp = -1,
                monthStr1 = StringItemDTO(name = "9月"),
                spending = 345.23f,
                income = 123.33f
            ),
        )
    )
}