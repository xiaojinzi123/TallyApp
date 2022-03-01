package com.xiaojinzi.module.support.module.fill_in_number.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.xiaojinzi.module.base.domain.CostEmptyState
import com.xiaojinzi.module.base.theme.Gray200
import com.xiaojinzi.module.base.theme.Green400
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.module.base.view.compose.BottomView
import com.xiaojinzi.module.base.view.compose.GridView
import com.xiaojinzi.module.support.R
import com.xiaojinzi.support.ktx.nothing

@Composable
private fun BillCreateKeyboardItemDecorateView(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable {
                onClick?.invoke()
            }
            .background(color = MaterialTheme.colors.background)
            // .background(color = Red800)
            .padding(all = 0.8.dp)
    ) {
        content()
    }
}

@Composable
private fun BillCreateKeyboardTextOrIconItemView(
    modifier: Modifier = Modifier,
    text: String? = null,
    @DrawableRes
    iconRsd: Int? = null,
    onClick: (() -> Unit)? = null,
) {
    BillCreateKeyboardItemDecorateView(
        modifier = modifier,
        onClick = onClick
    ) {
        text?.let { targetText ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    text = targetText,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                )
            }
        }
        iconRsd?.let { targetIconRsd ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painterResource(id = targetIconRsd),
                    modifier = Modifier.size(size = 24.dp),
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun BillCreateKeyboardCompleteOrUpdateItemView(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val vm: FillInNumberViewModel = viewModel()
    val canNext by vm.costUseCase.costIsCorrectFormatObservableDTO.collectAsState(initial = false)
    BillCreateKeyboardItemDecorateView(
        modifier = modifier,
        onClick = onClick,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .run {
                    if (canNext) {
                        this.background(color = Green400)
                    } else {
                        this.background(color = Gray200)
                    }
                }

        ) {
            Text(
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                text = stringResource(id = R.string.res_str_complete),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun BillCreateKeyboardNumberItemView(
    number: Int,
    onClick: (() -> Unit)? = null
) {
    BillCreateKeyboardTextOrIconItemView(
        text = number.toString(),
        onClick = onClick
    )
}

@ExperimentalFoundationApi
@Composable
private fun BillCreateKeyboardView() {
    val context = LocalContext.current
    val vm: FillInNumberViewModel = viewModel()
    BillCreateKeyboardItemDecorateView {
        GridView(
            items = (0..15).toList(),
            columnNumber = 4
        ) { item ->
            when (item) {
                in 0..2 -> BillCreateKeyboardNumberItemView(number = item + 7) {
                    vm.costUseCase.appendNumber(value = item + 7)
                    // vm.billCostUseCase.costAppend(target = (item + 7).toString())
                }
                in 4..6 -> BillCreateKeyboardNumberItemView(number = item) {
                    vm.costUseCase.appendNumber(value = item)
                    // vm.billCostUseCase.costAppend(target = item.toString())
                }
                in 8..10 -> BillCreateKeyboardNumberItemView(number = item - 7) {
                    vm.costUseCase.appendNumber(value = item - 7)
                    // vm.billCostUseCase.costAppend(target = (item - 7).toString())
                }
                3 -> BillCreateKeyboardTextOrIconItemView(iconRsd = R.drawable.res_delete1) {
                    vm.costUseCase.costDeleteLast()
                }
                7 -> BillCreateKeyboardTextOrIconItemView(text = "+") {
                    vm.costUseCase.appendAddSymbol()
                    // vm.billCostUseCase.costAppend(target = "+")
                }
                11 -> BillCreateKeyboardTextOrIconItemView(text = "-") {
                    vm.costUseCase.appendMinusSymbol()
                    // vm.billCostUseCase.costAppend(target = "-")
                }
                12 -> BillCreateKeyboardNumberItemView(number = 0) {
                    vm.costUseCase.appendNumber(value = 0)
                    // vm.billCostUseCase.costAppend(target = "0")
                }
                13 -> BillCreateKeyboardTextOrIconItemView(text = ".") {
                    vm.costUseCase.appendPoint()
                    // vm.billCostUseCase.costAppend(target = ".")
                }
                14 -> {}
                15 -> BillCreateKeyboardCompleteOrUpdateItemView() {
                    vm.onComplete(context = context)
                }
                else -> {
                    error("Not support")
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun FillInNumberView() {
    val vm: FillInNumberViewModel = viewModel()
    val costState by vm.costUseCase.costStrObservableDTO.collectAsState(initial = CostEmptyState())
    BottomView {
        Column(
            modifier = Modifier
                .clickable {
                    // empty
                }
                .background(color = MaterialTheme.colors.surface)
                .navigationBarsPadding()
                .nothing(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp)
                    .nothing(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 16.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_please_input1),
                    style = MaterialTheme.typography.subtitle1,
                )
                Spacer(modifier = Modifier.width(width = 16.dp))
                Text(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .wrapContentHeight()
                        .nothing(),
                    text = if (costState.strValue.isNullOrEmpty()) "0" else costState.strValue,
                    style = MaterialTheme.typography.subtitle1,
                    textAlign = TextAlign.End,
                )
            }
            BillCreateKeyboardView()
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun FillInNumberViewWrap() {
    FillInNumberView()
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun FillInNumberViewPreview() {
    FillInNumberView()
}