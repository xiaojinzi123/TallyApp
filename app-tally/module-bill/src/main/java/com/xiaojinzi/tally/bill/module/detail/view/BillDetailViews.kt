package com.xiaojinzi.tally.bill.module.detail.view

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.navigationBarsPadding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaojinzi.module.base.bean.INVALID_STRING
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.ImageDefault
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.module.base.view.compose.EmptyView
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.service.datasource.ReimburseType
import com.xiaojinzi.tally.base.service.datasource.TallyBillTypeDTO
import com.xiaojinzi.tally.base.support.route_api.billRouterApi
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.base.view.TallyLabelList
import com.xiaojinzi.tally.bill.R

@Composable
fun BillDetailItermView(
    title: String,
    onClick: (() -> Unit)? = null,
    canClick: Boolean = onClick != null,
    contentIsCenter: Boolean = true,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .run {
                if (canClick) {
                    this.clickable {
                        onClick?.invoke()
                    }
                } else {
                    this
                }
            }
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            .nothing(),
        verticalAlignment = if (contentIsCenter) Alignment.CenterVertically else Alignment.Top,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .padding(end = 24.dp)
        )
        Box(
            modifier = Modifier
                .weight(weight = 4f, fill = true),
            contentAlignment = Alignment.CenterEnd,
        ) {
            content()
        }
        if (canClick) {
            Spacer(modifier = Modifier.width(width = 4.dp))
            Image(
                modifier = Modifier
                    .size(size = 20.dp),
                painter = painterResource(id = R.drawable.res_arrow_right1),
                contentDescription = null
            )
        } else {
            /*Spacer(modifier = Modifier.width(width = 4.dp))
            Spacer(
                modifier = Modifier
                    .size(size = 20.dp)
                    .nothing(),
            )*/
        }
    }
}

@Composable
fun BillDetailTextItermView(
    title: String,
    value: String,
    onClick: (() -> Unit)? = null,
) {
    BillDetailItermView(
        title = title,
        onClick = onClick,
    ) {
        if (value.isEmpty()) {
            Text(text = stringResource(id = R.string.res_str_nothing))
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.wrapContentSize(),
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
fun BillDetailNormalItermView(
    title: String,
    content: @Composable () -> Unit
) {
    BillDetailItermView(title = title, canClick = false, contentIsCenter = false, content = content)
}

@Composable
fun BillDetailView(
    vo: BillDetailVO
) {
    val vm: BillDetailViewModel = viewModel()
    val context = LocalContext.current
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f, fill = true)
                .verticalScroll(state = rememberScrollState())
                .nothing(),
        ) {
            BillDetailTextItermView(
                title = stringResource(id = R.string.res_str_bill_type),
                value = vo.billType.nameItem.nameAdapter()
            )
            BillDetailTextItermView(
                title = stringResource(id = R.string.res_str_reimburse_type),
                value = vo.reimburseType.nameStringItem.nameAdapter()
            )
            val isShowBookInfo =
                vo.billType in listOf(TallyBillTypeDTO.Normal, TallyBillTypeDTO.Transfer)
            if (isShowBookInfo) {
                BillDetailTextItermView(
                    title = stringResource(id = R.string.res_str_ledger),
                    value = vo.bookName.nameAdapter()
                )
            }

            val isShowAccountInfo =
                vo.billType in listOf(TallyBillTypeDTO.Normal, TallyBillTypeDTO.Reimbursement)
            if (isShowAccountInfo) {
                BillDetailTextItermView(
                    title = stringResource(id = R.string.res_str_account),
                    value = vo.accountName.nameAdapter()
                )
            }

            when (vo.billType) {
                TallyBillTypeDTO.Normal -> {
                    val categoryGroupName = vo.categoryGroupName?.nameAdapter() ?: ""
                    val categoryName = vo.categoryName?.nameAdapter() ?: ""
                    BillDetailItermView(
                        title = stringResource(id = R.string.res_str_category),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(size = 20.dp),
                                painter = painterResource(id = vo.categoryIconAdapter),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(width = 4.dp))
                            Text(
                                text = "$categoryGroupName - $categoryName",
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.wrapContentSize()
                            )
                        }
                    }
                }
                else -> {}
            }

            when (vo.billType) {
                TallyBillTypeDTO.Normal, TallyBillTypeDTO.Reimbursement -> {
                    // 由于这里的花费是和保存的值是反过来的. 比如消费了 50, 在数据库中是 -50 保存着的
                    BillDetailTextItermView(
                        title = stringResource(id = R.string.res_str_money),
                        value = (if (vo.cost > 0) "+" else "") + vo.cost.tallyNumberFormat1()
                    )
                }
                TallyBillTypeDTO.Transfer -> {
                    BillDetailTextItermView(
                        title = stringResource(id = R.string.res_str_transfer),
                        value = "${-vo.cost} \n ${vo.accountName.nameAdapter()} → ${vo.transferTargetAccountName?.nameAdapter() ?: ""}"
                    )
                }
            }

            if (vo.reimburseBillCount > 0) {
                BillDetailTextItermView(
                    title = stringResource(id = R.string.res_str_the_cost_of_reimbursement),
                    value = (if (vo.reimburseBillCost > 0) "+" else "") + vo.reimburseBillCost.tallyNumberFormat1()
                )
                BillDetailTextItermView(
                    title = stringResource(id = R.string.res_str_balance),
                    value = (if (vo.costAdjust > 0) "+" else "") + vo.costAdjust.tallyNumberFormat1()
                )
                BillDetailTextItermView(
                    title = stringResource(id = R.string.res_str_reimbursement_bill_count),
                    value = vo.reimburseBillCount.toString(),
                ) {
                    vm.toSubReimbursementBillList(context = context)
                }
            }

            BillDetailTextItermView(
                title = stringResource(id = R.string.res_str_time),
                value = vo.createTimeStr
            ) {
                vm.toPickDateTime(context = context)
            }
            BillDetailItermView(
                title = stringResource(id = R.string.res_str_label),
                onClick = {
                    vm.toChooseLabel(context = context)
                },
            ) {
                if (vo.labelList.isNullOrEmpty()) {
                    Text(text = stringResource(id = R.string.res_str_nothing))
                } else {
                    TallyLabelList(labelList = vo.labelList)
                }
            }
            BillDetailTextItermView(
                title = stringResource(id = R.string.res_str_note),
                value = vo.note ?: ""
            ) {
                vm.toFillNote(context = context)
            }
            if (vo.imageUrlList.isNotEmpty()) {
                BillDetailNormalItermView(title = stringResource(id = R.string.res_str_picture)) {
                    FlowRow(
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp,
                    ) {
                        vo.imageUrlList.forEach { url ->
                            val imagePainter = rememberImagePainter(
                                data = url,
                                builder = {
                                    this.crossfade(enable = true)
                                    this.fallback(drawable = ImageDefault.FallbackImage)
                                    this.placeholder(drawable = ImageDefault.PlaceholderImage)
                                    this.error(drawable = ImageDefault.ErrorImage)
                                    this.allowHardware(enable = true)
                                    this.allowRgb565(enable = true)
                                    this.scale(scale = Scale.FILL)
                                }
                            )
                            Image(
                                painter = imagePainter,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(shape = MaterialTheme.shapes.small)
                                    .nothing()
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .nothing(),
        ) {
            when (vo.reimburseType) {
                ReimburseType.WaitReimburse, ReimburseType.Reimbursed -> {
                    val textRsd = when (vo.reimburseType) {
                        ReimburseType.WaitReimburse -> R.string.res_str_reimburse
                        ReimburseType.Reimbursed -> R.string.res_str_continue_reimburse
                        else -> notSupportError()
                    }
                    Button(
                        shape = MaterialTheme.shapes.small.copy(all = CornerSize(size = 0.dp)),
                        modifier = Modifier
                            .weight(weight = 1f, fill = true)
                            // .fillMaxWidth()
                            .wrapContentHeight()
                            .nothing(),
                        onClick = {
                            billRouterApi
                                .toBillCreateForCall(
                                    context = context,
                                    reimbursementBillId = vo.billId,
                                )
                                .forward()
                        }
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .navigationBarsPadding()
                                .padding(vertical = 8.dp)
                                .nothing(),
                            text = stringResource(id = textRsd),
                            style = MaterialTheme.typography.button,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.width(width = 2.dp))
                }
                else -> {}
            }
            Button(
                shape = MaterialTheme.shapes.small.copy(all = CornerSize(size = 0.dp)),
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    // .fillMaxWidth()
                    .wrapContentHeight()
                    .nothing(),
                onClick = {
                    vm.toBillEdit(context = context)
                }
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .navigationBarsPadding()
                        .padding(vertical = 8.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_edit),
                    style = MaterialTheme.typography.button,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BillDetailViewWrap() {
    val vm: BillDetailViewModel = viewModel()
    val context = LocalContext.current
    val targetVO: BillDetailVO? by vm.billDetailVOObservable.collectAsState(initial = null)
    Scaffold(
        topBar = {
            AppbarNormal(
                title = stringResource(id = R.string.res_str_bill_detail),
                menu1IconRsd = R.drawable.res_delete3,
                menu1ClickListener = {
                    MaterialAlertDialogBuilder(context)
                        .setMessage(R.string.res_str_is_confirm_to_delete)
                        .setNegativeButton(R.string.res_str_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.res_str_ensure) { dialog, _ ->
                            dialog.dismiss()
                            vm.deleteBillDetail()
                            context.tryFinishActivity()
                        }
                        .show()
                }
            )
        }
    ) {
        if (targetVO == null) {
            EmptyView(
                modifier = Modifier
                    .fillMaxSize(),
            )
        } else {
            BillDetailView(vo = targetVO!!)
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
fun BillDetailViewPreview() {
    TallyTheme {
        BillDetailView(
            vo = BillDetailVO(
                billId = INVALID_STRING,
                billType = TallyBillTypeDTO.Normal,
                reimburseType = ReimburseType.NoReimburse,
                bookName = StringItemDTO(
                    name = "测试"
                ),
                accountName = StringItemDTO(
                    name = "测试"
                ),
                transferTargetAccountName = StringItemDTO(
                    name = "测试"
                ),
                categoryIcon = R.drawable.res_category1,
                categoryGroupName = StringItemDTO(
                    name = "测试"
                ),
                categoryName = StringItemDTO(
                    name = "测试"
                ),
                cost = 100.00f,
                costAdjust = 100.00f,
                note = "测试的",
                createTime = System.currentTimeMillis(),
                reimburseBillCount = 1,
                reimburseBillCost = 50f,
            )
        )
    }
}