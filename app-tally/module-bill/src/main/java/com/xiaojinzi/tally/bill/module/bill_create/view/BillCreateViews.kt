package com.xiaojinzi.tally.bill.module.bill_create.view

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaojinzi.component.forwardForIntent
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.domain.CostEmptyState
import com.xiaojinzi.module.base.support.ImageDefault
import com.xiaojinzi.module.base.support.flow.toggle
import com.xiaojinzi.module.base.support.shake
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.module.base.theme.Gray200
import com.xiaojinzi.module.base.theme.Green400
import com.xiaojinzi.module.base.theme.Red900
import com.xiaojinzi.module.base.theme.Yellow800
import com.xiaojinzi.module.base.view.compose.GridView
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.tally.base.service.datasource.ReimburseType
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupDTO
import com.xiaojinzi.tally.base.service.datasource.TallyLabelDTO
import com.xiaojinzi.tally.base.support.settingService
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.base.theme.body3
import com.xiaojinzi.tally.base.theme.h7
import com.xiaojinzi.tally.bill.R
import com.xiaojinzi.tally.bill.module.bill_create.domain.ImageDTO
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
private fun BillCreateKeyboardItemDecorateView(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isVibrateDuring by settingService.vibrateDuringInputObservableDTO.collectAsState(initial = false)
    Surface(
        modifier = modifier
            .clickable {
                if (isVibrateDuring) {
                    shake()
                }
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
                    modifier = Modifier.padding(horizontal = 0.dp, vertical = 16.dp),
                    text = targetText,
                    style = MaterialTheme.typography.body2,
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
                    modifier = Modifier.size(size = 18.dp),
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun BillCreateKeyboardCompleteOrUpdateItemView(
    modifier: Modifier = Modifier,
    isUpdate: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val vm: BillCreateViewModel = viewModel()
    val canNext by vm.canNextObservableDTO.collectAsState(initial = false)
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
                text = stringResource(id = if (isUpdate) R.string.res_str_update else R.string.res_str_complete),
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
    val vm: BillCreateViewModel = viewModel()
    val isUpdate by vm.isUpdateBillObservableDTO.collectAsState(initial = false)
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
                12 -> BillCreateKeyboardTextOrIconItemView(text = stringResource(id = R.string.res_str_note)) {
                    vm.toFillNote(context = context)
                }
                13 -> BillCreateKeyboardNumberItemView(number = 0) {
                    vm.costUseCase.appendNumber(value = 0)
                    // vm.billCostUseCase.costAppend(target = "0")
                }
                14 -> BillCreateKeyboardTextOrIconItemView(text = ".") {
                    vm.costUseCase.appendPoint()
                    // vm.billCostUseCase.costAppend(target = ".")
                }
                15 -> BillCreateKeyboardCompleteOrUpdateItemView(
                    isUpdate = isUpdate
                ) {
                    vm.addOrUpdateBill(context = context)
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
private fun BillCreateTitleBarView() {
    val vm: BillCreateViewModel = viewModel()
    val context = LocalContext.current
    val tabSpace = 12.dp
    val isChinaLang = Locale.getDefault() == Locale.CHINA
    val textStyle = if (isChinaLang) {
        MaterialTheme.typography.subtitle2
    } else {
        MaterialTheme.typography.body3
    }
    val textSelectStyle = if (isChinaLang) {
        MaterialTheme.typography.subtitle1
    } else {
        MaterialTheme.typography.body2
    }
    val selectTabIndex: BillCreateTabType by vm
        .selectTabTypeObservableVO.collectAsState(initial = BillCreateTabType.Spending)
    val isReimbursementType by vm.isReimbursementTypeObservableDTO.collectAsState(initial = false)
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colors.primary)
            .statusBarsPadding()
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {
                context.tryFinishActivity()
            }) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.res_back1),
                    contentDescription = null
                )
            }
            if (isReimbursementType) {
                Text(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .wrapContentHeight()
                        .padding(horizontal = 0.dp, vertical = 12.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_reimburse),
                    style = MaterialTheme.typography.h6.copy(color = Color.White),
                    textAlign = TextAlign.Center,
                )
            } else {
                Row(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    listOf(
                        R.string.res_str_spending,
                        R.string.res_str_income,
                        R.string.res_str_transfer,
                    ).forEachIndexed { index, titleRsd ->
                        if (index > 0) {

                            Spacer(modifier = Modifier.width(width = tabSpace))
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .background(color = MaterialTheme.colors.background)
                                    .width(width = 1.dp)
                                    .fillMaxHeight(),
                            )
                            Spacer(modifier = Modifier.width(width = tabSpace))

                        }

                        Text(
                            modifier = Modifier
                                .weight(weight = 1f, fill = true)
                                .clickable {
                                    vm.selectTabTypeObservableVO.value =
                                        BillCreateTabType.fromIndex(index = index)
                                }
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                                .nothing(),
                            text = stringResource(id = titleRsd),
                            style = if (selectTabIndex.index == index) {
                                textSelectStyle
                            } else {
                                textStyle
                            }.copy(color = Color.White),
                            textAlign = TextAlign.Center,
                        )

                    }

                }
            }
            Spacer(modifier = Modifier.width(width = 48.dp))
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun BillCreateCategoryView(
    isShow: Boolean,
    vos: List<BillCreateGroupItemVO>,
    addNewClick: () -> Unit,
) {
    val context = LocalContext.current
    val vm: BillCreateViewModel = viewModel()
    val categorySelect: TallyCategoryDTO? by vm.categorySelectObservableDTO.collectAsState(
        initial = null
    )
    val isVibrateDuring by settingService.vibrateDuringInputObservableDTO.collectAsState(initial = false)
    AnimatedVisibility(
        visible = isShow,
    ) {
        GridView(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(shape = MaterialTheme.shapes.medium)
                .background(color = MaterialTheme.colors.background)
                .nothing(),
            items = vos,
            columnNumber = 5,
            lastItemContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 0.dp, vertical = 10.dp)
                        .clickable {
                            addNewClick.invoke()
                        }
                        .padding(horizontal = 0.dp, vertical = 10.dp)
                        .nothing(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier
                            .nothing(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            modifier = Modifier
                                .size(size = 32.dp)
                                .nothing(),
                            painter = painterResource(id = R.drawable.res_add3),
                            contentDescription = null
                        )
                    }
                }
            }
        ) { item ->
            val isSelect = (categorySelect?.uid == item.uid)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .combinedClickable(
                        onLongClick = {
                            if (isVibrateDuring) {
                                shake()
                            }
                            vm.toUpdateCate(context = context, cateId = item.uid)
                        }
                    ) {
                        if (isVibrateDuring) {
                            shake()
                        }
                        vm.selectCate(categoryId = item.uid)
                    }
                    .padding(horizontal = 2.dp, vertical = 10.dp)
                    .nothing(),
            ) {
                Box(
                    modifier = Modifier
                        .size(size = 28.dp)
                        .run {
                            if (isSelect) {
                                this
                                    .clip(shape = CircleShape)
                                    .background(color = Yellow800)
                                    .nothing()
                            } else {
                                this
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(size = 16.dp).nothing(),
                        painter = painterResource(id = item.iconRsd),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.height(height = 4.dp))
                Text(
                    text = item.name ?: stringResource(id = item.nameRsd!!),
                    style = MaterialTheme.typography.body3,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BillCreateGroupView(vos: List<BillCreateGroupVO>) {
    val context = LocalContext.current
    val vm: BillCreateViewModel = viewModel()
    val groupSelect: TallyCategoryGroupDTO? by vm.groupSelectObservableDTO.collectAsState(
        initial = null
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .nothing(),
    ) {
        GridView(
            items = vos, columnNumber = 5,
            afterRowContent = { rowIndex ->
                val selectIndex = vos.indexOfFirst { groupSelect?.uid == it.uid }
                val isShow = groupSelect != null &&
                        selectIndex in ((rowIndex) * 5 until (rowIndex + 1) * 5)
                val cateGroupVO = vos.elementAtOrNull(index = selectIndex)
                BillCreateCategoryView(
                    vos = cateGroupVO?.items ?: emptyList(),
                    isShow = isShow
                ) {
                    cateGroupVO?.let {
                        vm.toCreateCate(context = context, groupId = it.uid)
                    }
                }
            },
            lastItemContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            vm.toCreateCateGroup(context = context)
                        }
                        .padding(horizontal = 0.dp, vertical = 24.dp)
                        .nothing(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier
                            .size(size = 32.dp)
                            .nothing(),
                        painter = painterResource(id = R.drawable.res_add3),
                        contentDescription = null
                    )
                }
            },
        ) { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .combinedClickable(
                        onLongClick = {
                            vm.toUpdateCateGroup(
                                context = context,
                                cateGroupId = item.uid,
                            )
                        }
                    ) {
                        vm.selectCateGroup(groupId = item.uid)
                    }
                    .padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(size = 42.dp)
                        .run {
                            if (groupSelect?.uid == item.uid) {
                                this
                                    .clip(shape = CircleShape)
                                    .background(color = Yellow800)
                            } else {
                                this
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(size = 20.dp),
                        painter = painterResource(id = item.iconRsd),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.height(height = 6.dp))
                Text(
                    text = item.name ?: stringResource(id = item.nameRsd!!),
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun BillCreateTransferItemView(
    vo: BillCreateTransferVO?,
    onClick: (() -> Unit) = {}
) {
    Row(
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .nothing(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(size = 24.dp),
            painter = painterResource(id = vo?.iconRsd ?: R.drawable.res_none1),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(width = 12.dp))
        Divider(
            modifier = Modifier
                .width(width = 1.dp)
                .height(height = 20.dp)
                .nothing(),
        )
        Spacer(modifier = Modifier.width(width = 12.dp))
        if (vo == null) {
            Text(
                text = stringResource(id = R.string.res_str_please_select_account),
                style = MaterialTheme.typography.body2.copy(
                    color = Color.Gray
                ),
            )
        } else {
            Text(
                text = vo.nameAdapter(),
                style = MaterialTheme.typography.body2,
            )
        }
    }
}

@Composable
fun BillCreateTransferView() {
    val vm: BillCreateViewModel = viewModel()
    val context = LocalContext.current
    val outAccount by vm.outTransferAccountVO.collectAsState(initial = null)
    val inAccount by vm.inTransferAccountVO.collectAsState(initial = null)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp)
            .padding(horizontal = 32.dp)
            .nothing(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BillCreateTransferItemView(
            vo = outAccount
        ) {
            vm.billCreateTransferUseCase.toChooseOutAccount(context = context)
        }
        Spacer(modifier = Modifier.height(height = 20.dp))
        Image(
            modifier = Modifier
                .size(size = 32.dp)
                .clickable {
                    vm.billCreateTransferUseCase.toggleTransferAccount()
                }
                .nothing(),
            painter = painterResource(id = R.drawable.res_transfer2),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(height = 20.dp))
        BillCreateTransferItemView(
            vo = inAccount
        ) {
            vm.billCreateTransferUseCase.toChooseInAccount(context = context)
        }
    }
}

@Composable
fun BillCreateMenuListItemView(
    @DrawableRes
    iconRsd: Int,
    content: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .wrapContentSize()
            .padding(horizontal = 4.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.height(height = 24.dp))
        Image(
            modifier = Modifier
                .size(size = 16.dp),
            painter = painterResource(id = iconRsd), contentDescription = null
        )
        Spacer(modifier = Modifier.width(width = 4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.body3
        )
    }
}

@Composable
fun BillCreateMenuListCheckBoxItemView(
    checked: Boolean,
    content: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .wrapContentSize()
            .padding(horizontal = 4.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.height(height = 18.dp))
        Image(
            modifier = Modifier
                .size(size = 18.dp)
                .nothing(),
            painter = painterResource(id = if (checked) R.drawable.res_checkbox1_selected else R.drawable.res_checkbox1),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(width = 4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.body3
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun BillCreateMenuListView() {
    val context = LocalContext.current
    val vm: BillCreateViewModel = viewModel()
    // 目标时间
    val targetTime by vm.timeObservableDTO.collectAsState(initial = null)
    val targetAccount by vm.selectedAccountObservableDTO.collectAsState(initial = null)
    val targetBook by vm.selectedBookObservableDTO.collectAsState(initial = null)
    val isNotIncludedInIncomeAndExpenditure by vm.isNotIncludedInIncomeAndExpenditureObservableDTO.collectAsState(
        initial = false
    )
    val isReimburseType by vm.isReimbursementTypeObservableDTO.collectAsState(initial = false)
    val reimburseType by vm.reimburseTypeObservableDTO.collectAsState(
        initial = ReimburseType.NoReimburse
    )
    val selectTabIndex: BillCreateTabType by vm
        .selectTabTypeObservableVO.collectAsState(initial = BillCreateTabType.Spending)
    val isNormalBillTypeTabIndex = selectTabIndex.index in listOf(0, 1)
    val itemHSpace = 2.dp
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colors.surface)
            .fillMaxWidth()
            .wrapContentHeight()
            .horizontalScroll(state = rememberScrollState())
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BillCreateMenuListItemView(
            iconRsd = R.drawable.res_calendar1,
            content = if (targetTime == null) {
                stringResource(id = R.string.res_str_now)
            } else {
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(targetTime!!))
            }
        ) {
            // targetTime
            Router.with(context)
                .hostAndPath(RouterConfig.SYSTEM_DATE_TIME_PICKER)
                .apply {
                    targetTime?.let {
                        this.putLong("dateTime", it)
                    }
                }
                .requestCodeRandom()
                .forwardForIntent {
                    val targetDateTime = it.getLongExtra("dateTime", System.currentTimeMillis())
                    vm.timeObservableDTO.value = targetDateTime
                }
        }

        Spacer(modifier = Modifier.width(width = itemHSpace))
        AnimatedVisibility(visible = isNormalBillTypeTabIndex) {
            Spacer(modifier = Modifier.width(width = 4.dp))
            val accountStr = targetAccount?.run {
                this.name ?: stringResource(id = this.nameRsd!!)
            } ?: stringResource(id = R.string.res_str_default_account)
            BillCreateMenuListItemView(iconRsd = R.drawable.res_money1, content = accountStr) {
                vm.toChooseAccount(context = context)
            }
        }

        if (!isReimburseType) {

            Spacer(modifier = Modifier.width(width = itemHSpace))
            val bookStr = targetBook?.run {
                this.name ?: stringResource(id = this.nameRsd!!)
            } ?: stringResource(id = R.string.res_str_default_bill_book)
            BillCreateMenuListItemView(iconRsd = R.drawable.res_book1, content = bookStr) {
                vm.toChooseBook(context = context)
            }
        }

        Spacer(modifier = Modifier.width(width = itemHSpace))
        BillCreateMenuListItemView(
            iconRsd = R.drawable.res_image1,
            content = stringResource(id = R.string.res_str_add_picture)
        ) {
            vm.toChooseImage(context = context)
        }

        AnimatedVisibility(visible = isNormalBillTypeTabIndex && !isReimburseType) {
            Spacer(modifier = Modifier.width(width = itemHSpace))
            BillCreateMenuListItemView(
                iconRsd = R.drawable.res_reimbursement1,
                content = stringResource(
                    id = when (reimburseType) {
                        ReimburseType.NoReimburse -> R.string.res_str_no_reimbursement
                        ReimburseType.WaitReimburse -> R.string.res_str_reimbursement
                        ReimburseType.Reimbursed -> R.string.res_str_reimbursed
                    }
                )
            ) {
                vm.toChooseReimburseType(context = context)
            }
        }

        AnimatedVisibility(visible = isNormalBillTypeTabIndex) {
            Spacer(modifier = Modifier.width(width = itemHSpace))
            BillCreateMenuListCheckBoxItemView(
                checked = isNotIncludedInIncomeAndExpenditure,
                content = stringResource(id = R.string.res_str_not_included_in_income_and_expenditure)
            ) {
                vm.isNotIncludedInIncomeAndExpenditureObservableDTO.toggle()
            }
        }

    }
}

@ExperimentalFoundationApi
@Composable
fun BillCreateLabelContentView(vos: List<TallyLabelDTO>) {
    val context = LocalContext.current
    val vm: BillCreateViewModel = viewModel()
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .nothing(),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 4.dp,
    ) {
        vos.forEachIndexed { _, item ->
            Text(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.large)
                    .combinedClickable(
                        onLongClick = {
                            MaterialAlertDialogBuilder(context)
                                .setMessage(R.string.res_str_is_confirm_to_delete)
                                .setNegativeButton(R.string.res_str_cancel) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .setPositiveButton(R.string.res_str_ensure) { dialog, _ ->
                                    dialog.dismiss()
                                    vm.deleteLabel(labelId = item.uid)
                                }
                                .show()
                        }
                    ) {
                        // nothing
                    }
                    .background(
                        color = Color(color = item.colorInt),
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .nothing(),
                text = item.name.nameAdapter(),
                style = MaterialTheme.typography.body2.copy(
                    fontSize = 10.sp
                )
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun BillCreateImageContentView(vos: List<ImageDTO>) {
    val context = LocalContext.current
    val vm: BillCreateViewModel = viewModel()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(state = rememberScrollState())
            .background(color = MaterialTheme.colors.surface)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .nothing(),
        verticalAlignment = Alignment.Bottom,
    ) {
        vos.forEachIndexed { index, item ->
            if (index > 0) {
                Spacer(modifier = Modifier.width(width = 8.dp))
            }
            Image(
                painter = rememberImagePainter(
                    data = item.localFile ?: item.url,
                    builder = {
                        this.crossfade(enable = true)
                        this.fallback(drawable = ImageDefault.FallbackImage)
                        this.placeholder(drawable = ImageDefault.PlaceholderImage)
                        this.error(drawable = ImageDefault.ErrorImage)
                        this.allowHardware(enable = true)
                        this.allowRgb565(enable = true)
                        this.scale(scale = Scale.FILL)
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(shape = MaterialTheme.shapes.small)
                    .combinedClickable(
                        onLongClick = {
                            MaterialAlertDialogBuilder(context)
                                .setMessage(R.string.res_str_is_confirm_to_delete)
                                .setNegativeButton(R.string.res_str_cancel) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .setPositiveButton(R.string.res_str_ensure) { dialog, _ ->
                                    dialog.dismiss()
                                    vm.deleteImage(uid = item.uid)
                                }
                                .show()
                        }
                    ) {
                    }
                    .nothing()
            )
        }
    }
}

@Composable
fun BillCreateInputContentView() {
    val vm: BillCreateViewModel = viewModel()
    val context = LocalContext.current
    val noteStr by vm.noteStrObservableDTO.collectAsState(initial = null)
    val costStr by vm.costUseCase.costStrObservableDTO.collectAsState(initial = CostEmptyState())
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colors.surface)
            .nothing(),
        verticalAlignment = Alignment.Bottom,
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    vm.toChooseLabelList(context = context)
                }
                .wrapContentWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
                .padding(vertical = 12.dp)
                .padding(start = 16.dp)
                /*.background(color = MaterialTheme.colors.background)
                .padding(end = 1.dp)
                .background(color = MaterialTheme.colors.surface)*/
                .nothing(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.size(size = 16.dp),
                painter = painterResource(id = R.drawable.res_label1),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(horizontal = 5.dp),
                text = stringResource(id = R.string.res_str_label),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body2.copy(color = Yellow800)
            )
            Spacer(
                modifier = Modifier
                    .background(color = MaterialTheme.colors.background)
                    .width(width = 1.dp)
                    .fillMaxHeight(fraction = 0.8f)
            )
        }
        Text(
            modifier = Modifier
                .clickable {
                    vm.toFillNote(context = context)
                }
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .padding(top = 12.dp, bottom = 14.dp, start = 8.dp, end = 8.dp)
                .nothing(),
            text = noteStr.run {
                if (this.isNullOrEmpty()) {
                    stringResource(id = R.string.res_str_please_fill_in_the_note)
                } else {
                    if (this.length > 20) {
                        this.substring(startIndex = 0, endIndex = 17) + "..."
                    } else {
                        this
                    }
                }
            },
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography
                .body3
                .copy(
                    fontSize = 14.sp,
                )
                .run {
                    if (noteStr.isNullOrEmpty()) {
                        this.copy(
                            color = Color.Gray,
                        )
                    } else {
                        this
                    }
                }
        )
        Box(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .nothing(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .nothing(),
                text = if (costStr.length == 0) "0.00" else costStr.strValue,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Visible,
                style = MaterialTheme.typography.h7.copy(color = Red900)
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@InternalCoroutinesApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BillCreateView(
    inComeList: List<BillCreateGroupVO>,
    spendingList: List<BillCreateGroupVO>
) {
    val context = LocalContext.current
    val vm: BillCreateViewModel = viewModel()
    val isUpdateBill by vm.isUpdateBillObservableDTO.collectAsState(initial = false)
    val selectTabIndex: BillCreateTabType by vm
        .selectTabTypeObservableVO.collectAsState(initial = BillCreateTabType.Spending)
    val labelList by vm.selectedLabelListObservableDTO.collectAsState(initial = emptyList())
    val images: List<ImageDTO> by vm.selectedImageObservableDTO.collectAsState(initial = emptyList())
    val isShowImageUploadDialog by vm.isShowImageUploadDialogObservableDTO.collectAsState(initial = false)
    val imageUploadProgress by vm.imageUploadProgressObservableDTO.collectAsState(initial = 0f)
    val isReimbursementType by vm.isReimbursementTypeObservableDTO.collectAsState(initial = false)
    val reimburseTotalCost by vm.billCreateReimbursementUseCase.reimburseTotalCostObservableDTO.collectAsState(
        initial = null
    )
    val reimbursedCost by vm.billCreateReimbursementUseCase.reimbursedCostObservableDTO.collectAsState(
        initial = null
    )
    val reimburseRestCost by vm.billCreateReimbursementUseCase.reimburseRestCostObservableDTO.collectAsState(
        initial = null
    )
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colors.surface)
            .navigationBarsPadding()
            .fillMaxWidth()
            .nothing(),
    ) {
        if (isShowImageUploadDialog) {
            Dialog(
                onDismissRequest = {
                    // 不可以自己取消
                }
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .nothing(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.res_lottie_progress2)
                    )
                    val targetProgress by animateFloatAsState(targetValue = imageUploadProgress)
                    LottieAnimation(
                        modifier = Modifier
                            .width(200.dp)
                            .height(20.dp)
                            .nothing(),
                        composition = composition,
                        progress = targetProgress / 100f,
                    )
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    Text(
                        text = stringResource(id = R.string.res_str_picture_uploading),
                        style = MaterialTheme.typography.body1.copy(
                            color = Color.White,
                        ),
                    )
                }

            }
        }
        Column(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .nothing()
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .verticalScroll(state = rememberScrollState())
                    .nothing(),
            ) {
                // 标题
                BillCreateTitleBarView()
                if (isReimbursementType) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .nothing(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(height = 140.dp))
                        Column(
                            modifier = Modifier
                                .wrapContentWidth()
                                .wrapContentHeight()
                                .nothing(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 0.dp, vertical = 4.dp)
                                    .nothing(),
                                text = "总报销金额: ${reimburseTotalCost?.tallyNumberFormat1() ?: "---"}",
                                style = MaterialTheme.typography.h4,
                            )
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 0.dp, vertical = 4.dp)
                                    .nothing(),
                                text = "已报销金额: ${reimbursedCost?.tallyNumberFormat1() ?: "---"}",
                                style = MaterialTheme.typography.h4,
                            )
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 0.dp, vertical = 4.dp)
                                    .nothing(),
                                text = "剩余待报销金额: ${reimburseRestCost?.tallyNumberFormat1() ?: "---"}",
                                style = MaterialTheme.typography.h4,
                            )
                            if (isUpdateBill) {
                                Spacer(modifier = Modifier.height(height = 10.dp))
                                Button(
                                    modifier = Modifier
                                        .clip(shape = MaterialTheme.shapes.small)
                                        .nothing(),
                                    onClick = {
                                        reimburseRestCost?.tallyNumberFormat1()?.let {
                                            vm.costUseCase.costAppend(
                                                target = it,
                                                isReset = true,
                                            )
                                        }
                                    },
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 0.dp, vertical = 4.dp)
                                            .nothing(),
                                        text = "报销剩余部分金额",
                                        style = MaterialTheme.typography.h6,
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val pageState = rememberPagerState(initialPage = selectTabIndex.index)
                    LogSupport.d(
                        tag = "BillCreateView",
                        content = "rememberPagerState.initialPage = ${selectTabIndex.index}"
                    )
                    LogSupport.d(
                        tag = "BillCreateView",
                        content = "rememberPagerState.currentPage.index = ${pageState.currentPage}"
                    )
                    if (pageState.currentPage != selectTabIndex.index) {
                        scope.launch {
                            pageState.animateScrollToPage(page = selectTabIndex.index)
                            LogSupport.d(
                                tag = "BillCreateView",
                                content = "animateScrollToPage invoke"
                            )
                        }
                    }
                    LaunchedEffect(key1 = pageState) {
                        LogSupport.d(tag = "BillCreateView", content = "LaunchedEffect invoke")
                        snapshotFlow { pageState.currentPage }
                            .collect {
                                LogSupport.d(
                                    tag = "BillCreateView",
                                    content = "currentPage.index = $it"
                                )
                                vm.selectTabTypeObservableVO.value =
                                    BillCreateTabType.fromIndex(index = it)
                            }
                    }
                    HorizontalPager(
                        count = if (isReimbursementType) 2 else 3,
                        state = pageState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 1f, fill = true)
                            // .background(color = Color.Red)
                            .nothing(),
                        verticalAlignment = Alignment.Top,
                    ) { pageIndex ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(state = rememberScrollState())
                                .nothing(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            when (pageIndex) {
                                0 -> BillCreateGroupView(vos = spendingList)
                                // 1 -> BillCreateGroupView(vos = inComeList)
                                1 -> {
                                    val groupVO = inComeList.firstOrNull()
                                    BillCreateCategoryView(
                                        vos = groupVO?.items ?: emptyList(),
                                        isShow = true,
                                    ) {
                                        groupVO?.let {
                                            vm.toCreateCate(context = context, groupId = it.uid)
                                        }
                                    }
                                }
                                2 -> {
                                    BillCreateTransferView()
                                }
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colors.background)
                    .nothing()
            ) {
                Spacer(modifier = Modifier.height(height = 1.4.dp))
                if (!images.isNullOrEmpty()) {
                    images?.let {
                        BillCreateImageContentView(vos = it)
                    }
                    Spacer(modifier = Modifier.height(height = 1.4.dp))
                }
                if (!labelList.isNullOrEmpty()) {
                    BillCreateLabelContentView(vos = labelList)
                    Spacer(modifier = Modifier.height(height = 1.4.dp))
                }
                BillCreateInputContentView()
                Spacer(modifier = Modifier.height(height = 1.4.dp))
                BillCreateMenuListView()
                BillCreateKeyboardView()
            }
        }
    }

}

@InternalCoroutinesApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun BillCreateViewWrap() {
    val vm: BillCreateViewModel = viewModel()
    val inComeList by vm.inComeCategoryListObservableVO.collectAsState(initial = emptyList())
    val spendingList by vm.spendingCategoryListObservableVO.collectAsState(initial = emptyList())
    BillCreateView(
        inComeList = inComeList,
        spendingList = spendingList,
    )
}

@InternalCoroutinesApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
@Preview(locale = "zh")
@ExperimentalFoundationApi
private fun BillCreateViewPreview() {
    BillCreateView(
        inComeList = listOf(),
        spendingList = listOf()
    )
}

@Preview
@Composable
private fun BillCreateTransferItemViewPreview() {
    BillCreateTransferItemView(
        vo = BillCreateTransferVO(
            iconRsd = R.drawable.res_clean1,
            name = "饭卡"
        )
    )
}