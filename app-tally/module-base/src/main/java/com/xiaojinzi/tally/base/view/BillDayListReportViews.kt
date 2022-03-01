package com.xiaojinzi.tally.base.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.R
import com.xiaojinzi.tally.base.support.tallyNumberFormat1

@Composable
private fun BillDayListReportViewTitleItem() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .nothing(),
    ) {
        Text(
            text = stringResource(id = R.string.res_str_date),
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
private fun BillDayListReportViewItem(vo: BillDayVO) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .nothing(),
    ) {
        Text(
            text = vo.dayStr2 ?: "",
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.Start,
        )
        Text(
            text = vo.daySpendingCost.tallyNumberFormat1(),
            style = MaterialTheme.typography.body1.copy(
                color = Color.Red
            ),
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = vo.dayIncomeCost.tallyNumberFormat1(),
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = vo.dayCost.tallyNumberFormat1(),
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
fun BillDayListReportView(
    vos: List<BillDayVO>
) {
    val thresholdValue = 7
    var isFolding by remember { mutableStateOf(true) }
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
            BillDayListReportViewTitleItem()
            vos.forEachIndexed { index, item ->
                if (isFolding) {
                    if (index < thresholdValue) {
                        BillDayListReportViewItem(vo = item)
                    }
                } else {
                    BillDayListReportViewItem(vo = item)
                }
            }
            if (vos.size > thresholdValue) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable {
                            isFolding = !isFolding
                        }
                        .padding(horizontal = 0.dp, vertical = 8.dp)
                        .nothing(),
                    text = stringResource(id = if (isFolding) R.string.res_str_expand else R.string.res_str_pack_up),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
private fun BillDayListReportViewPreview() {
    BillDayListReportView(
        vos = listOf(
            BillDayVO(
                dayTime = System.currentTimeMillis(),
                dayStr1 = "日期1",
                dayStr2 = "日期2",
                dayOfWeek = R.string.res_str_monday,
                dayIncomeCost = 30f,
                daySpendingCost = -100f,
            ),
            BillDayVO(
                dayTime = System.currentTimeMillis(),
                dayStr1 = "日期1",
                dayStr2 = "日期2",
                dayOfWeek = R.string.res_str_monday,
                dayIncomeCost = 50f,
                daySpendingCost = -60f,
            ),
        )
    )
}